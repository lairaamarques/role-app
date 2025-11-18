package com.example.projetorole.ui.cupons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.repository.CupomNetworkRepository
import kotlinx.coroutines.launch

class CupomFormViewModel(
    private val repository: CupomNetworkRepository = CupomNetworkRepository()
) : ViewModel() {

    var titulo by mutableStateOf("")
    var descricao by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun criarCupom() {
        if (titulo.isBlank() || descricao.isBlank()) {
            errorMessage = "Preencha todos os campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val deuCerto = repository.criarCupom(titulo, descricao)

            if (deuCerto) {
                success = true
                titulo = ""
                descricao = ""
            } else {
                errorMessage = "Erro ao criar cupom. Tente novamente."
            }
            isLoading = false
        }
    }
}