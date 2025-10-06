package com.example.projetorole.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Evento
import com.example.projetorole.data.repository.EventoRepository
import com.example.projetorole.repository.EventoNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalheEventoViewModel(
    private val repository: EventoRepository = EventoNetworkRepository()
) : ViewModel() {

    private val _evento = MutableStateFlow<Evento?>(null)
    val evento: StateFlow<Evento?> = _evento.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCheckedIn = MutableStateFlow(false)
    val isCheckedIn: StateFlow<Boolean> = _isCheckedIn.asStateFlow()

    fun loadEvento(eventoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val lista = repository.getEventos()
            _evento.value = lista.find { it.id == eventoId }
            _isLoading.value = false
        }
    }

    fun fazerCheckin() {
        _evento.value?.let { atual ->
            _isCheckedIn.value = true
            _evento.value = atual.copy(checkIns = atual.checkIns + 1)
        }
    }
}