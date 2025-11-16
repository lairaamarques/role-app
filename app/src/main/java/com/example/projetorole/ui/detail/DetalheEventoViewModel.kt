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

sealed class CheckInUiState {
    object Idle : CheckInUiState()
    object Loading : CheckInUiState()
    object Success : CheckInUiState()
    data class Error(val message: String) : CheckInUiState()
}

class DetalheEventoViewModel(
    private val repository: EventoRepository = EventoNetworkRepository()
) : ViewModel() {

    private val _evento = MutableStateFlow<Evento?>(null)
    val evento: StateFlow<Evento?> = _evento.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadEvento(eventoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try{
                val lista = repository.getEventos()
                _evento.value = lista.find { it.id == eventoId }
            }
            catch (e: Exception){

            }
            _isLoading.value = false
        }
    }
    private val _checkInState = MutableStateFlow<CheckInUiState>(CheckInUiState.Idle)
    val checkInState = _checkInState.asStateFlow()

    fun performCheckIn(

        latitude: Double,
        longitude: Double
    ) {

        val eventoAtual = _evento.value
        if (eventoAtual == null) {
            _checkInState.value = CheckInUiState.Error("Evento não carregado.")
            return
        }

        viewModelScope.launch {
            _checkInState.value = CheckInUiState.Loading

            try {
                val response = repository.checkIn(
                    eventId = eventoAtual.id,
                    latitude = latitude,
                    longitude = longitude
                )

                if (response.success && response.data != null) {
                    _checkInState.value = CheckInUiState.Success

                    _evento.value = eventoAtual.copy(checkIns = eventoAtual.checkIns + 1)

                } else {

                    _checkInState.value = CheckInUiState.Error(response.message)
                }

            } catch (e: Exception) {

                _checkInState.value = CheckInUiState.Error(e.message ?: "Falha na comunicação")
            }
        }
    }

    fun resetCheckInState() {
        _checkInState.value = CheckInUiState.Idle
    }
}
