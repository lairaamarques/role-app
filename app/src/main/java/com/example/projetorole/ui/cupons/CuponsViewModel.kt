package com.example.projetorole.ui.cupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.data.repository.CupomRepository
import com.example.projetorole.repository.CupomNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CuponsViewModel(
    private val repository: CupomRepository = CupomNetworkRepository()
) : ViewModel() {

    private val _cupons = MutableStateFlow<List<Cupom>>(emptyList())
    val cupons: StateFlow<List<Cupom>> = _cupons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun carregarCupons() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { repository.getCupons() }
                .onSuccess { _cupons.value = it }
                .onFailure { _error.value = it.message ?: "Falha ao carregar cupons" }
            _isLoading.value = false
        }
    }

    fun usarCupom(cupomId: Int) {
        viewModelScope.launch {
            val sucesso = repository.usarCupom(cupomId)
            if (sucesso) {
                _cupons.update { lista ->
                    lista.map { if (it.id == cupomId) it.copy(usado = true) else it }
                }
            } else {
                _error.value = "Não foi possível marcar o cupom como usado"
            }
        }
    }

    fun deletarCupom(cupomId: Int) {
        viewModelScope.launch {
            val ok = kotlin.runCatching { repository.deleteCupom(cupomId) }.getOrDefault(false)
            if (ok) {
                _cupons.update { lista -> lista.filterNot { it.id == cupomId } }
            } else {
                _error.value = "Não foi possível excluir o cupom"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}