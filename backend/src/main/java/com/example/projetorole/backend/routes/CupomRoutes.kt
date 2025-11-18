package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.CupomRequest
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.ensureAuthenticated
import com.example.projetorole.backend.services.CupomService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureCupomRoutes() {

    route("/api/cupons") {

        get {
            if (call.ensureAuthenticated() == null) return@get

            try {
                val cupons = CupomService.listarCupons()
                call.respond(ApiResponse(true, "Cupons carregados", cupons))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Erro ao buscar cupons: ${e.message}"))
            }
        }

        post {
            val principal = call.ensureAuthenticated() ?: return@post

            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem criar cupons"))
                return@post
            }

            val request = runCatching { call.receive<CupomRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "Payload inválido"))
                return@post
            }

            try {
                val novoCupom = CupomService.criarCupom(request, principal.subjectId)
                call.respond(HttpStatusCode.Created, ApiResponse(true, "Cupom criado!", novoCupom))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Erro ao criar cupom: ${e.message}"))
            }
        }

        put("/{id}") {
            if (call.ensureAuthenticated() == null) return@put
            call.respond(HttpStatusCode.NotImplemented, ApiResponse<Unit>(false, "Edição ainda não implementada"))
        }

        delete("/{id}") {

            val principal = call.ensureAuthenticated() ?: return@delete

            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem gerenciar cupons"))
                return@delete
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@delete
            }

            val deletou = CupomService.deletarCupom(id, principal.subjectId)

            if (deletou) {
                call.respond(ApiResponse<Unit>(true, "Cupom removido com sucesso"))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Cupom não encontrado ou você não tem permissão"))
            }
        }
    }
}