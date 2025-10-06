package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.EventoRequest
import com.example.projetorole.backend.security.ensureAuthenticated
import com.example.projetorole.backend.services.EventoService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.configureEventoRoutes() {
    route("/api/eventos") {

        get {
            if (call.ensureAuthenticated() == null) return@get
            val eventos = EventoService.listarEventos()
            call.respond(ApiResponse(success = true, message = "Eventos carregados", data = eventos))
        }

        get("/{id}") {
            if (call.ensureAuthenticated() == null) return@get
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@get
            }

            val evento = EventoService.buscarEvento(id)
            if (evento != null) {
                call.respond(ApiResponse(success = true, message = "Evento encontrado", data = evento))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
            }
        }

        post {
            if (call.ensureAuthenticated() == null) return@post
            val payload = runCatching { call.receive<EventoRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "Payload inválido"))
                return@post
            }

            val created = EventoService.criarEvento(payload)
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Evento criado", data = created))
        }

        put("/{id}") {
            if (call.ensureAuthenticated() == null) return@put
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@put
            }

            val payload = runCatching { call.receive<EventoRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "Payload inválido"))
                return@put
            }

            val updated = EventoService.atualizarEvento(id, payload)
            if (updated != null) {
                call.respond(ApiResponse(success = true, message = "Evento atualizado", data = updated))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
            }
        }

        delete("/{id}") {
            if (call.ensureAuthenticated() == null) return@delete
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@delete
            }

            val removed = EventoService.removerEvento(id)
            if (removed) {
                call.respond(ApiResponse<Unit>(success = true, message = "Evento removido"))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
            }
        }

        post("/{id}/checkin") {
            if (call.ensureAuthenticated() == null) return@post
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@post
            }

            val updated = EventoService.registrarCheckIn(id)
            if (updated != null) {
                call.respond(ApiResponse(success = true, message = "Check-in registrado", data = updated))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
            }
        }
    }
}