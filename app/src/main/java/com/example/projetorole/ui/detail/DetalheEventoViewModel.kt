package com.example.projetorole.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Evento
import com.example.projetorole.data.repository.EventoRepositoryMock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalheEventoViewModel : ViewModel() {
    
    private val repository = EventoRepositoryMock()
    
    // Estado do evento atual
    private val _evento = MutableStateFlow<Evento?>(null)
    val evento: StateFlow<Evento?> = _evento.asStateFlow()
    
    // Estado de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado do check-in
    private val _isCheckedIn = MutableStateFlow(false)
    val isCheckedIn: StateFlow<Boolean> = _isCheckedIn.asStateFlow()
    
    // Carregar evento por ID
    fun loadEvento(eventoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // CORREÇÃO: usar repository.eventos.value ao invés de repository.getEventos()
            val eventos = repository.eventos.value
            val eventoEncontrado = eventos.find { it.id == eventoId }
            
            _evento.value = eventoEncontrado
            _isLoading.value = false
        }
    }
    
    // Fazer check-in
    fun fazerCheckin() {
        viewModelScope.launch {
            _evento.value?.let { evento ->
                // Simular check-in
                _isCheckedIn.value = true
                
                // Incrementar contador de check-ins
                _evento.value = evento.copy(checkIns = evento.checkIns + 1)
            }
        }
    }
    
    // Comprar ingresso (placeholder)
    fun comprarIngresso() {
        viewModelScope.launch {
            // TODO: Implementar lógica de compra
            // Por enquanto apenas placeholder
        }
    }
    
    // Ver rota (placeholder)
    fun verRota() {
        viewModelScope.launch {
            // TODO: Implementar abertura do GPS/Maps
            // Por enquanto apenas placeholder
        }
    }
}