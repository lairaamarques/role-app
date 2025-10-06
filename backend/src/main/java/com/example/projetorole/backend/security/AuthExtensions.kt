package com.example.projetorole.backend.security

import com.example.projetorole.backend.models.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun ApplicationCall.ensureAuthenticated(): Int? {
    val token = request.headers["Authorization"]
        ?.removePrefix("Bearer ")
        ?.trim()

    val userId = TokenManager.getUserId(token)
    if (userId == null) {
        respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Token inválido"))
    }
    return userId
}