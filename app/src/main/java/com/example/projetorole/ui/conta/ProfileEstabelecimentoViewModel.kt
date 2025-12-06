package com.example.projetorole.ui.conta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileEstabelecimentoViewModel(
    private val fetchEventos: suspend () -> List<Any> = { emptyList() },
    private val fetchCupons: suspend () -> List<Any> = { emptyList() },
    private val fetchCheckins: suspend () -> List<Any> = { emptyList() }
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _totalEventos = MutableStateFlow(0)
    val totalEventos: StateFlow<Int> = _totalEventos

    private val _cuponsAtivos = MutableStateFlow(0)
    val cuponsAtivos: StateFlow<Int> = _cuponsAtivos

    private val _totalCheckins = MutableStateFlow(0)
    val totalCheckins: StateFlow<Int> = _totalCheckins

    init {
        carregarEstatisticas()
    }

    fun carregarEstatisticas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val eventos = runCatching { fetchEventos() }.getOrDefault(emptyList())
                val cupons = runCatching { fetchCupons() }.getOrDefault(emptyList())
                val checkins = runCatching { fetchCheckins() }.getOrDefault(emptyList())

                _totalEventos.value = eventos.size
                _cuponsAtivos.value = cupons.size
                _totalCheckins.value = checkins.size
            } catch (ex: Throwable) {
                _error.value = ex.message ?: "Erro ao carregar estatísticas"
            } finally {
                _isLoading.value = false
            }
        }
    }
}