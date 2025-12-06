package com.example.projetorole.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.ActorType
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.EstabelecimentoAuthResponseNetwork
import com.example.projetorole.network.EstabelecimentoLoginRequestNetwork
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
import com.example.projetorole.data.auth.AuthDataStore
import android.content.Context

class LoginEstabelecimentoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onSenhaChange(value: String) = _uiState.update { it.copy(senha = value) }

    fun login() {
        val email = _uiState.value.email.trim()
        val senha = _uiState.value.senha

        if (email.isBlank() || senha.isBlank()) {
            _uiState.update { it.copy(error = "Informe e-mail e senha") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }

            val result = runCatching {
                ApiClient.client.post("${ApiClient.BASE_URL}/api/estabelecimentos/login") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        EstabelecimentoLoginRequestNetwork(
                            email = email,
                            senha = senha
                        )
                    )
                }.body<ApiResponse<EstabelecimentoAuthResponseNetwork>>()
            }

            result.onSuccess { response ->
                val data = response.data
                if (response.success && data != null) {
                    val rawPhoto = data.estabelecimento.fotoUrl
                    val photoUrl = rawPhoto?.let { if (it.startsWith("/")) ApiClient.BASE_URL + it else it }
                    AuthRepository.setSession(
                        token = data.token,
                        actorType = ActorType.ESTAB,
                        displayName = data.estabelecimento.nomeFantasia,
                        email = data.estabelecimento.email,
                        photoUrl = photoUrl
                    )
                    _uiState.update { it.copy(isLoading = false, success = true) }
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
                        error = throwable.message ?: "Erro ao realizar login"
                    )
                }
            }
        }
    }

    fun consumeSuccess() = _uiState.update { it.copy(success = false) }
}