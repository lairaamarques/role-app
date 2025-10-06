package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.LoginRequest
import com.example.projetorole.backend.models.RegisterRequest
import com.example.projetorole.backend.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.configureAuthRoutes() {
    post("/api/login") {
        val payload = runCatching { call.receive<LoginRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "Payload inválido"))
            return@post
        }

        val result = AuthService.login(payload)
        if (result == null) {
            call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Credenciais inválidas"))
        } else {
            call.respond(ApiResponse(success = true, message = "Login realizado", data = result))
        }
    }

    post("/api/register") {
        val payload = runCatching { call.receive<RegisterRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "Payload inválido"))
            return@post
        }

        when (val result = AuthService.register(payload)) {
            is AuthService.RegisterResult.Success -> call.respond(
                HttpStatusCode.Created,
                ApiResponse(success = true, message = "Conta criada com sucesso", data = result.usuario)
            )
            is AuthService.RegisterResult.Failure -> call.respond(
                result.status,
                ApiResponse<Unit>(success = false, message = result.message)
            )
        }
    }
}