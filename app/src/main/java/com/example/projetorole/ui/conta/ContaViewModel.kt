package com.example.projetorole.ui.conta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Usuario(
    val nome: String,
    val idade: Int,
    val avatarUrl: String? = null,
    val checkinsRealizados: Int,
    val checkinsSalvos: Int
)

data class DiaAgenda(
    val dia: Int,
    val diaSemana: String,
    val temEvento: Boolean = true
)

class ContaViewModel : ViewModel() {

    private val _usuario = MutableStateFlow(
        Usuario(
            nome = "Messias Assunção",
            idade = 20,
            checkinsRealizados = 152,
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

    fun logout() {
        viewModelScope.launch {
            AuthRepository.clearToken()
        }
    }
}