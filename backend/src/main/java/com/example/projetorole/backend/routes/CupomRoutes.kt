package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.ensureAuthenticated
import com.example.projetorole.backend.services.CupomUsuarioService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route

fun Route.configureCupomRoutes() {
    route("/api/cupons") {
        get {
            val principal = call.ensureAuthenticated() ?: return@get
            if (principal.subjectType != SubjectType.USER) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas usuários podem ver seus cupons"))
                return@get
            }
            runCatching { CupomUsuarioService.listarCuponsDoUsuario(principal.subjectId) }
                .onSuccess { cupons -> call.respond(ApiResponse(true, "Cupons carregados", cupons)) }
                .onFailure { call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Erro ao buscar cupons")) }
        }

        post("/{id}/usar") {
            val principal = call.ensureAuthenticated() ?: return@post
            if (principal.subjectType != SubjectType.USER) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas usuários podem usar cupons"))
                return@post
            }
            val cupomId = call.parameters["id"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))

            if (CupomUsuarioService.marcarComoUsado(cupomId, principal.subjectId)) {
                call.respond(ApiResponse<Unit>(true, "Cupom marcado como usado"))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Cupom não encontrado ou já utilizado"))
            }
        }

        delete("/{id}") {
            val principal = call.ensureAuthenticated() ?: return@delete
            if (principal.subjectType != SubjectType.USER) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas usuários podem excluir seus cupons"))
                return@delete
            }
            val cupomId = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))

            val deleted = CupomUsuarioService.deletarCupom(cupomId, principal.subjectId)
            if (deleted) {
                call.respond(ApiResponse<Unit>(true, "Cupom excluído"))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Cupom não encontrado"))
            }
        }

    }
}