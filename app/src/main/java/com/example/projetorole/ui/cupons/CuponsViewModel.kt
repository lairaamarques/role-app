package com.example.projetorole.ui.cupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.repository.CupomNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CuponsViewModel(
    private val repository: CupomNetworkRepository = CupomNetworkRepository()
) : ViewModel() {

    private val _cupons = MutableStateFlow<List<Cupom>>(emptyList())
    val cupons: StateFlow<List<Cupom>> = _cupons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        carregarCupons()
    }

    fun carregarCupons() {
        viewModelScope.launch {
            _isLoading.value = true
            val lista = repository.getCupons()
            _cupons.value = lista
            _isLoading.value = false
        }
    }

    fun deletarCupom(cupomId: Int) {
        viewModelScope.launch {
            val sucesso = repository.deletarCupom(cupomId)
            if (sucesso) {
                _cupons.value = _cupons.value.filter { it.id != cupomId }
            }
        }
    }
}