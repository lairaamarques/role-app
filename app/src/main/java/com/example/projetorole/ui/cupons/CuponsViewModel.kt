package com.example.projetorole.ui.cupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.data.repository.CupomRepository
import com.example.projetorole.repository.CupomNetworkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CuponsViewModel(
    private val repository: CupomRepository = CupomNetworkRepository()
) : ViewModel() {

    val cupons: StateFlow<List<Cupom>> = repository.cupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}