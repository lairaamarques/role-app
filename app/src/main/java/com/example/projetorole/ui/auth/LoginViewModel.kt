package com.example.projetorole.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.LoginRequest
import com.example.projetorole.network.LoginResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onSenhaChange(value: String) {
        _uiState.update { it.copy(senha = value) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val senha = _uiState.value.senha

        if (email.isEmpty() || senha.isEmpty()) {
            _uiState.update { it.copy(error = "Informe e-mail e senha") }
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = runCatching {
                ApiClient.client.post("${ApiClient.BASE_URL}/api/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(email = email, senha = senha))
                }.body<ApiResponse<LoginResponse>>()
            }

            result.onSuccess { response ->
                if (response.success && response.data != null) {
                    AuthRepository.setToken(response.data.token)
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message.ifBlank { "Falha no login" }
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
}