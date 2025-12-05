package com.example.projetorole.ui.conta

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.UsuarioNetwork
import com.example.projetorole.network.EstabelecimentoAuthResponseNetwork
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.client.request.put
import io.ktor.client.request.forms.*
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel : ViewModel() {

    fun submitProfile(context: Context, name: String?, imageUri: Uri?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val actor = AuthRepository.currentActorType
                val path = if (actor == com.example.projetorole.data.auth.ActorType.ESTAB) "/api/estabelecimentos/me" else "/api/me/profile"

                val form = formData {
                    if (!name.isNullOrBlank()) {
                        if (actor == com.example.projetorole.data.auth.ActorType.ESTAB) append("nomeFantasia", name.trim())
                        else append("nome", name.trim())
                    }
                    if (imageUri != null) {
                        context.contentResolver.openInputStream(imageUri)?.use { input ->
                            val bytes = input.readBytes()
                            append(
                                key = "image",
                                bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"profile.jpg\"")
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                }
                            )
                        }
                    }
                }

                val responseText = withContext(Dispatchers.IO) {
                    ApiClient.client.put("${ApiClient.BASE_URL}$path") {
                        setBody(MultiPartFormDataContent(form))
                    }.body<String>()
                }

                runCatching {
                    val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    if (actor == com.example.projetorole.data.auth.ActorType.ESTAB) {
                        val parsed = json.decodeFromString(
                            ApiResponse.serializer(EstabelecimentoAuthResponseNetwork.serializer()),
                            responseText
                        )
                        if (parsed.success && parsed.data != null) {
                            val estab = parsed.data.estabelecimento
                            val photoUrl = estab.fotoUrl?.let {
                                if (it.startsWith("/")) ApiClient.BASE_URL + it else it 
                            }
                            AuthRepository.setSession(
                                AuthRepository.currentToken,
                                AuthRepository.currentActorType,
                                estab.nomeFantasia,
                                estab.email,
                                photoUrl
                            )
                            onResult(true, null)
                        } else onResult(false, parsed.message)
                    } else {
                        val parsed = json.decodeFromString(
                            ApiResponse.serializer(UsuarioNetwork.serializer()),
                            responseText
                        )
                        if (parsed.success && parsed.data != null) {
                            val user = parsed.data
                            val photoUrl = user.fotoUrl?.let { 
                                if (it.startsWith("/")) ApiClient.BASE_URL + it else it 
                            }
                            AuthRepository.setSession(
                                AuthRepository.currentToken,
                                AuthRepository.currentActorType,
                                user.nome,
                                user.email,
                                photoUrl
                            )
                            onResult(true, null)
                        } else onResult(false, parsed.message)
                    }
                }.getOrElse { ex ->
                    onResult(false, ex.message ?: "Erro ao atualizar perfil")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro de rede")
            }
        }
    }
}