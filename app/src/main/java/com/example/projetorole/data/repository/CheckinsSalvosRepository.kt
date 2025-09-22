package com.example.projetorole.data.repository

import com.example.projetorole.data.model.CheckinSalvo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckinsSalvosRepository {
    private val _checkinsSalvos = MutableStateFlow<List<CheckinSalvo>>(emptyList())
    val checkinsSalvos: StateFlow<List<CheckinSalvo>> = _checkinsSalvos.asStateFlow()
    
    fun salvarCheckin(eventoId: Int) {
        val salvosAtual = _checkinsSalvos.value.toMutableList()
        if (!salvosAtual.any { it.eventoId == eventoId }) {
            salvosAtual.add(CheckinSalvo(eventoId))
            _checkinsSalvos.value = salvosAtual
        }
    }
    
    fun removerCheckinSalvo(eventoId: Int) {
        _checkinsSalvos.value = _checkinsSalvos.value.filter { it.eventoId != eventoId }
    }
    
    fun isCheckinSalvo(eventoId: Int): Boolean {
        return _checkinsSalvos.value.any { it.eventoId == eventoId }
    }
    
    fun getCheckinsSalvos(): List<CheckinSalvo> {
        return _checkinsSalvos.value
    }
}