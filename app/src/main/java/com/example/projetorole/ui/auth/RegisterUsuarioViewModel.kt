package com.example.projetorole.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.RegisterRequest
import com.example.projetorole.network.UsuarioNetwork
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUsuarioUiState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterUsuarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUsuarioUiState())
    val uiState: StateFlow<RegisterUsuarioUiState> = _uiState.asStateFlow()

    fun onNomeChange(value: String) = _uiState.update { it.copy(nome = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onSenhaChange(value: String) = _uiState.update { it.copy(senha = value) }
    fun onConfirmarSenhaChange(value: String) = _uiState.update { it.copy(confirmarSenha = value) }

    fun registrar() {
        val state = _uiState.value
        val email = state.email.trim()
        val senha = state.senha
        val confirmar = state.confirmarSenha

        if (email.isBlank() || senha.isBlank() || confirmar.isBlank()) {
            _uiState.update { it.copy(error = "Preencha todos os campos obrigatórios") }
            return
        }

        if (senha != confirmar) {
            _uiState.update { it.copy(error = "As senhas não coincidem") }
            return
        }

        if (senha.length < 6) {
            _uiState.update { it.copy(error = "Senha deve ter pelo menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }

            val resultado = runCatching {
                ApiClient.client.post("${ApiClient.BASE_URL}/api/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        RegisterRequest(
                            nome = state.nome.trim().takeIf { it.isNotBlank() },
                            email = email,
                            senha = senha
                        )
                    )
                }.body<ApiResponse<UsuarioNetwork>>()
            }

            resultado.onSuccess { response ->
                if (response.success) {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message.ifBlank { "Não foi possível criar a conta" }
                        )
                    }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Erro ao conectar ao servidor"
                    )
                }
            }
        }
    }

    fun consumeSuccess() = _uiState.update { it.copy(success = false) }
    fun clearError() = _uiState.update { it.copy(error = null) }
}