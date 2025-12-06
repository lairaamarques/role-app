package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.EstabelecimentoAuthResponse
import com.example.projetorole.backend.models.EstabelecimentoDTO
import com.example.projetorole.backend.models.EstabelecimentoLoginRequest
import com.example.projetorole.backend.models.EstabelecimentoRegisterRequest
import com.example.projetorole.backend.services.EstabelecimentoAuthService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.io.File
import java.util.UUID
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.ensureAuthenticated
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import org.jetbrains.exposed.sql.select

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

    put("/api/estabelecimentos/me") {
        val principal = call.ensureAuthenticated() ?: return@put
        if (principal.subjectType != SubjectType.ESTAB) {
            call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas estabelecimentos podem editar este perfil"))
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

        val nomeFantasia = fields["nomeFantasia"]?.takeIf { it.isNotBlank() }?.trim()
        val foto = savedImagePath ?: fields["fotoUrl"]?.takeIf { it.isNotBlank() }?.trim()

        val updated = transaction {
            EstabelecimentosTable.update({ EstabelecimentosTable.id eq principal.subjectId }) {
                if (nomeFantasia != null) it[EstabelecimentosTable.nomeFantasia] = nomeFantasia
                it[EstabelecimentosTable.fotoUrl] = foto
            } > 0
        }

        if (!updated) {
            call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Estabelecimento não encontrado"))
            return@put
        }

        val dto = transaction {
            EstabelecimentosTable.select { EstabelecimentosTable.id eq principal.subjectId }.single().let { row ->
                EstabelecimentoDTO(
                    id = row[EstabelecimentosTable.id].value,
                    email = row[EstabelecimentosTable.email],
                    nomeFantasia = row[EstabelecimentosTable.nomeFantasia],
                    cnpj = row[EstabelecimentosTable.cnpj],
                    fotoUrl = row[EstabelecimentosTable.fotoUrl]
                )
            }
        }

        call.respond(ApiResponse(success = true, message = "Perfil atualizado", data = EstabelecimentoAuthResponse(token = "", estabelecimento = dto)))
    }
}