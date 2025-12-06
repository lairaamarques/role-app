package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.LoginRequest
import com.example.projetorole.backend.models.RegisterRequest
import com.example.projetorole.backend.services.AuthService
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
import com.example.projetorole.backend.models.Users
import com.example.projetorole.backend.models.UsuarioDTO
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.ensureAuthenticated
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import org.jetbrains.exposed.sql.select

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

    put("/api/me/profile") {
        val principal = call.ensureAuthenticated() ?: return@put
        if (principal.subjectType != SubjectType.USER) {
            call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas usuários podem editar este perfil"))
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

        val nome = fields["nome"]?.takeIf { it.isNotBlank() }?.trim()
        val foto = savedImagePath ?: fields["fotoUrl"]?.takeIf { it.isNotBlank() }?.trim()

        val updated = transaction {
            Users.update({ Users.id eq principal.subjectId }) {
                it[Users.nome] = nome
                it[Users.fotoUrl] = foto
            } > 0
        }

        if (!updated) {
            call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "Usuário não encontrado"))
            return@put
        }

        val dto = transaction {
            Users.select { Users.id eq principal.subjectId }.single().let { row ->
                UsuarioDTO(
                    id = row[Users.id].value,
                    email = row[Users.email],
                    nome = row[Users.nome],
                    fotoUrl = row[Users.fotoUrl]
                )
            }
        }

        call.respond(ApiResponse(success = true, message = "Perfil atualizado", data = dto))
    }
}