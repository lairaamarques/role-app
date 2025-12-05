package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.CheckInRequest
import com.example.projetorole.backend.models.EventoRequest
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.ensureAuthenticated
import com.example.projetorole.backend.services.DistanceCheckException
import com.example.projetorole.backend.services.EventNotFoundException
import com.example.projetorole.backend.services.EventoService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.io.File
import java.util.*

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
            val principal = call.ensureAuthenticated() ?: return@post
            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem criar eventos"))
                return@post
            }

            val multipart = call.receiveMultipart()
            val fields = mutableMapOf<String, String?>()
            var savedImagePath: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fields[part.name ?: ""] = part.value
                    }
                    is PartData.FileItem -> {
                        if ((part.name ?: "") == "image") {
                            val uploadsDir = File("uploads")
                            if (!uploadsDir.exists()) uploadsDir.mkdirs()
                            val ext = ContentType.parse(part.contentType?.toString() ?: "")?.toString()?.let { "" } ?: ""
                            val filename = "${UUID.randomUUID()}.jpg"
                            val file = File(uploadsDir, filename)
                            part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyTo(it) } }
                            savedImagePath = "/uploads/$filename"
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            val nome = fields["nome"] ?: ""
            val local = fields["local"] ?: ""
            val horario = fields["horario"] ?: ""
            val pago = (fields["pago"] ?: "false").toBoolean()
            val preco = fields["preco"]?.toDoubleOrNull()
            val descricao = fields["descricao"]?.takeIf { it.isNotBlank() }
            val latitude = fields["latitude"]?.toDoubleOrNull() ?: 0.0
            val longitude = fields["longitude"]?.toDoubleOrNull() ?: 0.0
            val cupomTitulo = fields["cupomTitulo"]?.takeIf { it.isNotBlank() }
            val cupomDescricao = fields["cupomDescricao"]?.takeIf { it.isNotBlank() }
            val cupomCheckins = fields["cupomCheckinsNecessarios"]?.toIntOrNull() ?: 1
            val paymentLink = fields["paymentLink"]?.takeIf { it.isNotBlank() }
            val imageUrl = savedImagePath ?: fields["imageUrl"]?.takeIf { it.isNotBlank() }

            val payload = EventoRequest(
                nome = nome,
                local = local,
                horario = horario,
                pago = pago,
                preco = preco,
                descricao = descricao,
                estabelecimentoId = null,
                latitude = latitude,
                longitude = longitude,
                cupomTitulo = cupomTitulo,
                cupomDescricao = cupomDescricao,
                cupomCheckinsNecessarios = cupomCheckins,
                paymentLink = paymentLink,
                imageUrl = imageUrl
            )

            val sanitized = payload.sanitized()
            val validationError = validateEventoRequest(sanitized)
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, validationError))
                return@post
            }

            val created = EventoService.criarEvento(sanitized, principal.subjectId)
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Evento criado", data = created))
        }

        put("/{id}") {
            val principal = call.ensureAuthenticated() ?: return@put
            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem editar eventos"))
                return@put
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@put
            }

            val multipart = call.receiveMultipart()
            val fields = mutableMapOf<String, String?>()
            var savedImagePath: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> fields[part.name ?: ""] = part.value
                    is PartData.FileItem -> {
                        if ((part.name ?: "") == "image") {
                            val uploadsDir = File("uploads")
                            if (!uploadsDir.exists()) uploadsDir.mkdirs()
                            val filename = "${UUID.randomUUID()}.jpg"
                            val file = File(uploadsDir, filename)
                            part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyTo(it) } }
                            savedImagePath = "/uploads/$filename"
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            val nome = fields["nome"] ?: ""
            val local = fields["local"] ?: ""
            val horario = fields["horario"] ?: ""
            val pago = (fields["pago"] ?: "false").toBoolean()
            val preco = fields["preco"]?.toDoubleOrNull()
            val descricao = fields["descricao"]?.takeIf { it.isNotBlank() }
            val latitude = fields["latitude"]?.toDoubleOrNull() ?: 0.0
            val longitude = fields["longitude"]?.toDoubleOrNull() ?: 0.0
            val cupomTitulo = fields["cupomTitulo"]?.takeIf { it.isNotBlank() }
            val cupomDescricao = fields["cupomDescricao"]?.takeIf { it.isNotBlank() }
            val cupomCheckins = fields["cupomCheckinsNecessarios"]?.toIntOrNull() ?: 1
            val paymentLink = fields["paymentLink"]?.takeIf { it.isNotBlank() }
            val imageUrl = savedImagePath ?: fields["imageUrl"]?.takeIf { it.isNotBlank() }

            val payload = EventoRequest(
                nome = nome,
                local = local,
                horario = horario,
                pago = pago,
                preco = preco,
                descricao = descricao,
                estabelecimentoId = null,
                latitude = latitude,
                longitude = longitude,
                cupomTitulo = cupomTitulo,
                cupomDescricao = cupomDescricao,
                cupomCheckinsNecessarios = cupomCheckins,
                paymentLink = paymentLink,
                imageUrl = imageUrl
            )

            val sanitized = payload.sanitized()
            val validationError = validateEventoRequest(sanitized)
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, validationError))
                return@put
            }

            when (val result = EventoService.atualizarEvento(id, sanitized, principal.subjectId)) {
                is EventoService.UpdateResult.Success -> call.respond(ApiResponse(success = true, message = "Evento atualizado", data = result.response))
                EventoService.UpdateResult.NotFound -> call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
                EventoService.UpdateResult.Forbidden -> call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Você não tem permissão"))
            }
        }

        delete("/{id}") {
            val principal = call.ensureAuthenticated() ?: return@delete
            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem remover eventos"))
                return@delete
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID inválido"))
                return@delete
            }

            when (val result = EventoService.removerEvento(id, principal.subjectId)) {
                EventoService.DeleteResult.Success -> {
                    call.respond(ApiResponse<Unit>(success = true, message = "Evento removido com sucesso"))
                }
                EventoService.DeleteResult.NotFound -> {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Evento não encontrado"))
                }
                EventoService.DeleteResult.Forbidden -> {
                    call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Você não tem permissão para remover este evento"))
                }
            }
        }

        post("/{id}/checkin") {

            val principal = call.ensureAuthenticated() ?: return@post
            if (principal.subjectType != SubjectType.USER) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ApiResponse<Unit>(false, "Apenas usuários podem fazer check-in")
                )
                return@post
            }

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "ID de evento inválido"))
                return@post
            }

            val request = runCatching { call.receive<CheckInRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, "Corpo da requisição (lat/lon) inválido ou ausente"))
                return@post
            }

            try {
                val dto = EventoService.realizarCheckIn(principal.subjectId, id, request.latitude, request.longitude)
                call.respond(ApiResponse(success = true, message = "Check-in realizado", data = dto))
            } catch (e: EventNotFoundException) {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, e.message ?: "Evento não encontrado", null))
            } catch (e: DistanceCheckException) {
                call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, e.message ?: "Distância inválida", null))
            } catch (e: com.example.projetorole.backend.services.DuplicateCheckInException) {
                call.respond(HttpStatusCode.Conflict, ApiResponse<Unit>(false, e.message ?: "Check-in já realizado"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Erro ao processar check-in"))
            }
        }
    }

    route("/api/estabelecimentos") {
        get("/me/eventos") {
            val principal = call.ensureAuthenticated() ?: return@get
            if (principal.subjectType != SubjectType.ESTAB) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ApiResponse<Unit>(success = false, message = "Apenas estabelecimentos podem acessar seus eventos")
                )
                return@get
            }

            val eventos = EventoService.listarEventosDoEstabelecimento(principal.subjectId)
            call.respond(
                ApiResponse(success = true, message = "Eventos do estabelecimento", data = eventos)
            )
        }
    }
}

private val horarioIsoRegex = Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}\$")

private fun validateEventoRequest(payload: EventoRequest): String? {
    if (payload.nome.isBlank()) return "O nome do evento não pode estar vazio"
    if (payload.local.isBlank()) return "O local do evento não pode estar vazio"
    if (!horarioIsoRegex.matches(payload.horario)) return "O horário do evento deve estar no formato correto"
    return null
}

private fun EventoRequest.sanitized(): EventoRequest = copy(
    nome = nome.trim(),
    local = local.trim(),
    horario = horario.trim()
)