package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.LoginRequest
import com.example.projetorole.backend.models.LoginResponse
import com.example.projetorole.backend.models.RegisterRequest
import com.example.projetorole.backend.models.UsuarioDTO
import com.example.projetorole.backend.models.Users
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.TokenManager
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object AuthService {

    private val emailRegex =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
    private const val MIN_PASSWORD_LENGTH = 6

    suspend fun login(request: LoginRequest): LoginResponse? = withContext(Dispatchers.IO) {
        val normalizedEmail = request.email.trim().lowercase()

        val row = transaction {
            Users.select { Users.email eq normalizedEmail }.singleOrNull()
        } ?: return@withContext null

        val senhaConfere = BCrypt.checkpw(request.senha, row[Users.senhaHash])
        if (!senhaConfere) {
            null
        } else {
            val userId = row[Users.id].value
            val token = TokenManager.createToken(userId, SubjectType.USER)
            LoginResponse(
                token = token,
                usuario = UsuarioDTO(
                    id = userId,
                    email = row[Users.email],
                    nome = row[Users.nome],
                    fotoUrl = row[Users.fotoUrl]
                )
            )
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResult = withContext(Dispatchers.IO) {
        val email = request.email.trim().lowercase()
        val nome = request.nome?.trim()?.takeIf { it.isNotEmpty() }

        if (!emailRegex.matches(email)) {
            return@withContext RegisterResult.Failure(
                status = HttpStatusCode.BadRequest,
                message = "E-mail inválido"
            )
        }

        if (request.senha.length < MIN_PASSWORD_LENGTH) {
            return@withContext RegisterResult.Failure(
                status = HttpStatusCode.BadRequest,
                message = "Senha deve ter pelo menos $MIN_PASSWORD_LENGTH caracteres"
            )
        }

        val senhaHash = BCrypt.hashpw(request.senha, BCrypt.gensalt(10))

        transaction {
            if (Users.select { Users.email eq email }.count() > 0) {
                return@transaction RegisterResult.Failure(
                    status = HttpStatusCode.Conflict,
                    message = "E-mail já cadastrado"
                )
            }

            val id = Users.insertAndGetId {
                it[Users.email] = email
                it[Users.senhaHash] = senhaHash
                it[Users.nome] = nome
                it[Users.fotoUrl] = null
            }.value

            RegisterResult.Success(
                UsuarioDTO(
                    id = id,
                    email = email,
                    nome = nome,
                    fotoUrl = null
                )
            )
        }
    }

    sealed class RegisterResult {
        data class Success(val usuario: UsuarioDTO) : RegisterResult()
        data class Failure(val status: HttpStatusCode, val message: String) : RegisterResult()
    }
}