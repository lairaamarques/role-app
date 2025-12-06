package com.example.projetorole.data.repository

import android.content.Context
import com.example.projetorole.data.model.CheckinSalvo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object CheckinsSalvosRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var initialized = false
    private lateinit var appContext: Context

    private val _checkinsSalvos = MutableStateFlow<List<CheckinSalvo>>(emptyList())
    val checkinsSalvos: StateFlow<List<CheckinSalvo>> = _checkinsSalvos.asStateFlow()

    fun init(context: Context) {
        if (initialized) return
        appContext = context.applicationContext
        initialized = true
        scope.launch {
            CheckinsDataStore.getSavedIdsFlow(appContext).collect { ids ->
                _checkinsSalvos.value = ids.map { CheckinSalvo(it) }
            }
        }
    }

    private suspend fun persistCurrent() {
        CheckinsDataStore.saveIds(appContext, _checkinsSalvos.value.map { it.eventoId })
    }

    fun salvarCheckin(eventoId: Int) {
        if (!initialized) return
        val salvosAtual = _checkinsSalvos.value.toMutableList()
        if (!salvosAtual.any { it.eventoId == eventoId }) {
            salvosAtual.add(CheckinSalvo(eventoId))
            _checkinsSalvos.value = salvosAtual
            scope.launch { persistCurrent() }
        }
    }

    fun removerCheckinSalvo(eventoId: Int) {
        if (!initialized) return
        _checkinsSalvos.value = _checkinsSalvos.value.filter { it.eventoId != eventoId }
        scope.launch { persistCurrent() }
    }

    fun isCheckinSalvo(eventoId: Int): Boolean {
        return _checkinsSalvos.value.any { it.eventoId == eventoId }
    }

    fun getCheckinsSalvos(): List<CheckinSalvo> {
        return _checkinsSalvos.value
    }
}