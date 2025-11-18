package com.example.projetorole.ui.conta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

data class Usuario(
    val nome: String,
    val email: String,
    val checkinsRealizados: Int,
    val checkinsSalvos: Int
)

data class DiaAgenda(
    val dia: Int,
    val diaSemana: String,
    val temEvento: Boolean = true
)

class ContaViewModel(private val navController: NavHostController? = null) : ViewModel() {

    private val _usuario = MutableStateFlow(
        Usuario(
            nome = "Usuário Rolê",
            email = "",
            checkinsRealizados = 7,
            checkinsSalvos = 1
        )
    )
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()

    private val _diasAgenda = MutableStateFlow(
        listOf(
            DiaAgenda(21, "DOM"),
            DiaAgenda(22, "SEG"),
            DiaAgenda(23, "TER"),
            DiaAgenda(24, "QUA"),
            DiaAgenda(25, "QUI"),
            DiaAgenda(26, "SEX"),
            DiaAgenda(27, "SAB"),
            DiaAgenda(28, "DOM"),
            DiaAgenda(29, "SEG"),
            DiaAgenda(30, "TER")
        )
    )
    val diasAgenda: StateFlow<List<DiaAgenda>> = _diasAgenda.asStateFlow()

    init {
        viewModelScope.launch {
            AuthRepository.profile.collect { profile ->
                val nome = profile?.displayName?.takeIf { it.isNotBlank() } ?: "Usuário Rolê"
                val email = profile?.email.orEmpty()
                _usuario.value = _usuario.value.copy(nome = nome, email = email)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                AuthRepository.clearToken()
                delay(100)
                navController?.navigate("authOptions") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                println("Erro no logout: ${e.message}")
            }
        }
    }
}