package com.example.projetorole.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Evento
import com.example.projetorole.repository.EstabelecimentoEventosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyEventsUiState(
    val eventos: List<Evento> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MyEventsViewModel(
    private val repository: EstabelecimentoEventosRepository = EstabelecimentoEventosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState(isLoading = true))
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadEventos()
    }

    fun loadEventos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { repository.getMeusEventos() }
                .onSuccess { eventos -> _uiState.value = MyEventsUiState(eventos = eventos) }
                .onFailure { throwable ->
                    _uiState.value = MyEventsUiState(
                        error = throwable.message ?: "Não foi possível carregar os eventos"
                    )
                }
        }
    }

    fun deleteEvento(id: Int, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.deletarEvento(id) }
                .onSuccess { success ->
                    if (success) {
                        loadEventos()
                        callback(true, null)
                    } else {
                        callback(false, "Não foi possível remover o evento")
                    }
                }
                .onFailure { throwable ->
                    callback(false, throwable.message ?: "Erro ao remover o evento")
                }
        }
    }
}