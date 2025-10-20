package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.EstabelecimentoLoginRequest
import com.example.projetorole.backend.models.EstabelecimentoRegisterRequest
import com.example.projetorole.backend.services.EstabelecimentoAuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.configureEstabelecimentoAuthRoutes() {

    post("/api/estabelecimentos/register") {
        val payload = runCatching { call.receive<EstabelecimentoRegisterRequest>() }.getOrElse {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Unit>(success = false, message = "Payload inválido")
            )
            return@post
        }

        when (val result = EstabelecimentoAuthService.register(payload)) {
            is EstabelecimentoAuthService.RegisterResult.Success -> call.respond(
                HttpStatusCode.Created,
                ApiResponse(
                    success = true,
                    message = "Estabelecimento cadastrado com sucesso",
                    data = result.estabelecimento
                )
            )

            is EstabelecimentoAuthService.RegisterResult.Failure -> call.respond(
                result.status,
                ApiResponse<Unit>(success = false, message = result.message)
            )
        }
    }

    post("/api/estabelecimentos/login") {
        val payload = runCatching { call.receive<EstabelecimentoLoginRequest>() }.getOrElse {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Unit>(success = false, message = "Payload inválido")
            )
            return@post
        }

        val result = EstabelecimentoAuthService.login(payload)
        if (result == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiResponse<Unit>(success = false, message = "Credenciais inválidas")
            )
        } else {
            call.respond(
                ApiResponse(
                    success = true,
                    message = "Login realizado",
                    data = result
                )
            )
        }
    }
}