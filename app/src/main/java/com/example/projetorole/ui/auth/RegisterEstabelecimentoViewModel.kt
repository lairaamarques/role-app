package com.example.projetorole.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.EstabelecimentoNetwork
import com.example.projetorole.network.EstabelecimentoRegisterRequestNetwork
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

data class RegisterEstabelecimentoUiState(
    val nomeFantasia: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val cnpj: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterEstabelecimentoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterEstabelecimentoUiState())
    val uiState: StateFlow<RegisterEstabelecimentoUiState> = _uiState.asStateFlow()

    fun onNomeFantasiaChange(value: String) = _uiState.update { it.copy(nomeFantasia = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onSenhaChange(value: String) = _uiState.update { it.copy(senha = value) }
    fun onConfirmarSenhaChange(value: String) = _uiState.update { it.copy(confirmarSenha = value) }
    fun onCnpjChange(value: String) = _uiState.update { it.copy(cnpj = value) }

    fun registrar() {
        val state = _uiState.value
        val email = state.email.trim()
        val senha = state.senha
        val confirmar = state.confirmarSenha
        val nomeFantasia = state.nomeFantasia.trim()

        if (email.isBlank() || senha.isBlank() || confirmar.isBlank() || nomeFantasia.isBlank()) {
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
                ApiClient.client.post("${ApiClient.BASE_URL}/api/estabelecimentos/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        EstabelecimentoRegisterRequestNetwork(
                            email = email,
                            senha = senha,
                            nomeFantasia = nomeFantasia,
                            cnpj = state.cnpj.takeIf { it.isNotBlank() }
                        )
                    )
                }.body<ApiResponse<EstabelecimentoNetwork>>()
            }

            resultado.onSuccess { response ->
                if (response.success) {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message.ifBlank { "Não foi possível cadastrar" }
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