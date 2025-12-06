package com.example.projetorole.ui.conta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.data.repository.CheckinsSalvosRepository
import com.example.projetorole.network.CheckInDTO
import com.example.projetorole.repository.CheckinNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Usuario(
    val nome: String,
    val email: String,
    val checkinsRealizados: Int,
    val checkinsSalvos: Int
)

class ContaViewModel(private val navController: androidx.navigation.NavHostController? = null) : ViewModel() {

    private val _usuario = MutableStateFlow(
        Usuario(
            nome = "Usuário Rolê",
            email = "",
            checkinsRealizados = 0,
            checkinsSalvos = 0
        )
    )
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()

    private val checkinsSalvosRepo = CheckinsSalvosRepository
    private val checkinNetworkRepo = CheckinNetworkRepository()

    init {
        viewModelScope.launch {
            AuthRepository.profile.collect { profile ->
                val nome = profile?.displayName?.takeIf { it.isNotBlank() } ?: "Usuário Rolê"
                val email = profile?.email.orEmpty()
                _usuario.value = _usuario.value.copy(nome = nome, email = email)
            }
        }

        viewModelScope.launch {
            AuthRepository.token.collect { token ->
                if (!token.isNullOrBlank()) {
                    carregarCheckinsRealizados()
                } else {
                    _usuario.value = _usuario.value.copy(checkinsRealizados = 0)
                }
            }
        }

        viewModelScope.launch {
            checkinsSalvosRepo.checkinsSalvos.collect { salvos ->
                _usuario.value = _usuario.value.copy(checkinsSalvos = salvos.size)
            }
        }
    }

    fun carregarCheckinsRealizados() {
        viewModelScope.launch {
            val tokenSnapshot = AuthRepository.currentToken
            if (tokenSnapshot.isNullOrBlank()) return@launch

            try {
                val lista: List<CheckInDTO> = checkinNetworkRepo.getMyCheckins()
                if (AuthRepository.currentToken == tokenSnapshot) {
                    _usuario.value = _usuario.value.copy(checkinsRealizados = lista.size)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                AuthRepository.clearToken()
                kotlinx.coroutines.delay(100)
                navController?.navigate("authOptions") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                println("Erro no logout: ${e.message}")
            }
        }
    }
}