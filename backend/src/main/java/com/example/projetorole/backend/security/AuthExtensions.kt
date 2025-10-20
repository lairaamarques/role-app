package com.example.projetorole.backend.security

import com.example.projetorole.backend.models.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

data class AuthPrincipal(
    val subjectId: Int,
    val subjectType: SubjectType
)

suspend fun ApplicationCall.ensureAuthenticated(): AuthPrincipal? {
    val token = request.headers["Authorization"]
        ?.removePrefix("Bearer ")
        ?.trim()

    val payload = TokenManager.getPayload(token)
    if (payload == null) {
        respond(
            HttpStatusCode.Unauthorized,
            ApiResponse<Unit>(success = false, message = "Token inválido")
        )
        return null
    }
    return AuthPrincipal(payload.subjectId, payload.subjectType)
}