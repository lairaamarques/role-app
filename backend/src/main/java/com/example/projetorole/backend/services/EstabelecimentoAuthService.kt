package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.EstabelecimentoAuthResponse
import com.example.projetorole.backend.models.EstabelecimentoDTO
import com.example.projetorole.backend.models.EstabelecimentoLoginRequest
import com.example.projetorole.backend.models.EstabelecimentoRegisterRequest
import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.security.SubjectType
import com.example.projetorole.backend.security.TokenManager
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object EstabelecimentoAuthService {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
    private const val MIN_PASSWORD_LENGTH = 6

    suspend fun register(request: EstabelecimentoRegisterRequest): RegisterResult =
        withContext(Dispatchers.IO) {
            val email = request.email.trim().lowercase()
            val nomeFantasia = request.nomeFantasia.trim()

            if (!emailRegex.matches(email)) {
                return@withContext RegisterResult.Failure(
                    HttpStatusCode.BadRequest, "E-mail inválido"
                )
            }

            if (nomeFantasia.isEmpty()) {
                return@withContext RegisterResult.Failure(
                    HttpStatusCode.BadRequest, "Informe o nome fantasia"
                )
            }

            if (request.senha.length < MIN_PASSWORD_LENGTH) {
                return@withContext RegisterResult.Failure(
                    HttpStatusCode.BadRequest,
                    "Senha deve ter pelo menos $MIN_PASSWORD_LENGTH caracteres"
                )
            }

            val senhaHash = BCrypt.hashpw(request.senha, BCrypt.gensalt(10))

            transaction {
                val exists = EstabelecimentosTable
                    .select { EstabelecimentosTable.email eq email }
                    .count() > 0

                if (exists) {
                    return@transaction RegisterResult.Failure(
                        HttpStatusCode.Conflict, "E-mail já cadastrado"
                    )
                }

                val id = EstabelecimentosTable.insertAndGetId {
                    it[EstabelecimentosTable.email] = email
                    it[EstabelecimentosTable.senhaHash] = senhaHash
                    it[EstabelecimentosTable.nomeFantasia] = nomeFantasia
                    it[EstabelecimentosTable.cnpj] = request.cnpj
                    it[EstabelecimentosTable.fotoUrl] = request.fotoUrl
                }.value

                RegisterResult.Success(
                    EstabelecimentoDTO(
                        id = id,
                        email = email,
                        nomeFantasia = nomeFantasia,
                        cnpj = request.cnpj,
                        fotoUrl = request.fotoUrl
                    )
                )
            }
        }

    suspend fun login(request: EstabelecimentoLoginRequest): EstabelecimentoAuthResponse? =
        withContext(Dispatchers.IO) {
            val email = request.email.trim().lowercase()

            val row = transaction {
                EstabelecimentosTable
                    .select { EstabelecimentosTable.email eq email }
                    .singleOrNull()
            } ?: return@withContext null

            val senhaConfere =
                BCrypt.checkpw(request.senha, row[EstabelecimentosTable.senhaHash])
            if (!senhaConfere) return@withContext null

            val id = row[EstabelecimentosTable.id].value
            val token = TokenManager.createToken(id, SubjectType.ESTAB)
            EstabelecimentoAuthResponse(
                token = token,
                estabelecimento = EstabelecimentoDTO(
                    id = id,
                    email = row[EstabelecimentosTable.email],
                    nomeFantasia = row[EstabelecimentosTable.nomeFantasia],
                    cnpj = row[EstabelecimentosTable.cnpj],
                    fotoUrl = row[EstabelecimentosTable.fotoUrl]
                )
            )
        }

    sealed class RegisterResult {
        data class Success(val estabelecimento: EstabelecimentoDTO) : RegisterResult()
        data class Failure(val status: HttpStatusCode, val message: String) : RegisterResult()
    }
}