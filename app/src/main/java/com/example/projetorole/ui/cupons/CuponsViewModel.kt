package com.example.projetorole.ui.cupons

import androidx.lifecycle.ViewModel
import com.example.projetorole.data.repository.CupomRepositoryMock
import com.example.projetorole.data.model.Cupom
import kotlinx.coroutines.flow.StateFlow

class CuponsViewModel : ViewModel() {
    private val repository = CupomRepositoryMock()
    
    val cupons: StateFlow<List<Cupom>> = repository.cupons
    
    fun usarCupom(cupomId: Int) {
        repository.usarCupom(cupomId)
    }
}