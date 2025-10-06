package com.example.projetorole.network

import com.example.projetorole.data.auth.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    const val BASE_URL = "http://10.0.2.2:8080"

    val client = HttpClient(Android) {
        expectSuccess = false

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        defaultRequest {
            AuthRepository.currentToken?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    AuthRepository.notifyUnauthorized()
                }
            }
            handleResponseExceptionWithRequest { cause, _ ->
                val exception = cause as? ResponseException ?: return@handleResponseExceptionWithRequest
                if (exception.response.status == HttpStatusCode.Unauthorized) {
                    AuthRepository.notifyUnauthorized()
                }
            }
        }
    }
}