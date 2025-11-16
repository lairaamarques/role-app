package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.models.EventoRequest
import com.example.projetorole.backend.models.EventoResponse
import com.example.projetorole.backend.models.EventosTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import com.example.projetorole.backend.models.CheckInDTO
import com.example.projetorole.backend.models.CheckIns
import java.time.LocalDateTime

class EventNotFoundException : Exception("Evento não encontrado.")
class DistanceCheckException : Exception("Usuário muito longe do evento.")

object EventoService {

    private const val MAX_DISTANCE_IN_KILOMETERS = 0.1
    private val eventoJoinEstabelecimento =
        EventosTable.leftJoin(
            otherTable = EstabelecimentosTable,
            onColumn = { estabelecimento },
            otherColumn = { EstabelecimentosTable.id }
        )
    fun listarEventos(): List<EventoResponse> = transaction {
        eventoJoinEstabelecimento.selectAll().map { it.toEventoResponse() }
    }

    fun buscarEvento(id: Int): EventoResponse? = transaction {
        eventoJoinEstabelecimento
            .select { EventosTable.id eq id }
            .singleOrNull()
            ?.toEventoResponse()
    }
    fun criarEvento(request: EventoRequest, establishmentId: Int): EventoResponse = transaction {
        val nomeSanitizado = request.nome.trim()
        val localSanitizado = request.local.trim()
        val horarioSanitizado = request.horario.trim()

        val insertedId = EventosTable.insert {
            it[nome] = nomeSanitizado
            it[local] = localSanitizado
            it[horario] = horarioSanitizado
            it[pago] = request.pago
            it[preco] = request.preco
            it[descricao] = request.descricao // Adicionado salvamento da descricao
            it[estabelecimento] = EntityID(establishmentId, EstabelecimentosTable)

            it[latitude] = request.latitude
            it[longitude] = request.longitude
        } get EventosTable.id

        eventoJoinEstabelecimento
            .select { EventosTable.id eq insertedId.value }
            .single()
            .toEventoResponse()
    }

    sealed class UpdateResult {
        data class Success(val response: EventoResponse) : UpdateResult()
        data object NotFound : UpdateResult()
        data object Forbidden : UpdateResult()
    }

    fun atualizarEvento(id: Int, request: EventoRequest, establishmentId: Int): UpdateResult = transaction {
        val current = EventosTable
            .select { EventosTable.id eq id }
            .singleOrNull() ?: return@transaction UpdateResult.NotFound

        val ownerId = current[EventosTable.estabelecimento]?.value
        if (ownerId == null || ownerId != establishmentId) {
            return@transaction UpdateResult.Forbidden
        }

        EventosTable.update({ EventosTable.id eq id }) {
            it[nome] = request.nome.trim()
            it[local] = request.local.trim()
            it[horario] = request.horario.trim()
            it[pago] = request.pago
            it[preco] = request.preco
            it[descricao] = request.descricao
        }

        val updated = eventoJoinEstabelecimento
            .select { EventosTable.id eq id }
            .single()
            .toEventoResponse()

        UpdateResult.Success(updated)
    }

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data object NotFound : DeleteResult()
        data object Forbidden : DeleteResult()
    }

    fun removerEvento(id: Int, establishmentId: Int): DeleteResult = transaction {
        val current = EventosTable
            .select { EventosTable.id eq id }
            .singleOrNull() ?: return@transaction DeleteResult.NotFound

        val ownerId = current[EventosTable.estabelecimento]?.value
        if (ownerId == null || ownerId != establishmentId) {
            return@transaction DeleteResult.Forbidden
        }

        val removed = EventosTable.deleteWhere { EventosTable.id eq id } > 0
        if (removed) DeleteResult.Success else DeleteResult.NotFound
    }

    fun realizarCheckIn(
        userIdParam: Int,
        eventId: Int,
        userLatitude: Double,
        userLongitude: Double
    ): CheckInDTO = transaction {

        val event = EventosTable.select { EventosTable.id eq eventId }
            .firstOrNull() ?: throw EventNotFoundException()

        val eventLatitude = event[EventosTable.latitude]
        val eventLongitude = event[EventosTable.longitude]

        val distance = DistanceCalculator.calculate(
            userLatitude, userLongitude,
            eventLatitude, eventLongitude
        )

        if (distance > MAX_DISTANCE_IN_KILOMETERS) {
            throw DistanceCheckException()
        }

        val newCheckIn = CheckIns.insert {
            it[userId] = userIdParam
            it[CheckIns.eventoId] = eventId
            it[validatedAt] = LocalDateTime.now()
            it[createdAt] = LocalDateTime.now()
        }

        val novoTotal = event[EventosTable.checkIns] + 1
        EventosTable.update({ EventosTable.id eq eventId }) {
            it[checkIns] = novoTotal
        }

        return@transaction CheckInDTO(
            id = newCheckIn[CheckIns.id].value,
            userId = newCheckIn[CheckIns.userId].value,
            eventoId = newCheckIn[CheckIns.eventoId].value,
            validatedAt = newCheckIn[CheckIns.validatedAt]?.toString(),
            createdAt = newCheckIn[CheckIns.createdAt].toString()
        )
    }

    fun listarEventosDoEstabelecimento(establishmentId: Int): List<EventoResponse> = transaction {
        val entity = EntityID(establishmentId, EstabelecimentosTable)
        eventoJoinEstabelecimento
            .select { EventosTable.estabelecimento eq entity }
            .map { it.toEventoResponse() }
    }

    private fun ResultRow.toEventoResponse(checkInsOverride: Int? = null) = EventoResponse(
        id = this[EventosTable.id].value,
        nome = this[EventosTable.nome],
        local = this[EventosTable.local],
        horario = this[EventosTable.horario],
        checkIns = checkInsOverride ?: this[EventosTable.checkIns],
        pago = this[EventosTable.pago],
        preco = this[EventosTable.preco],
        descricao = this[EventosTable.descricao],
        estabelecimentoId = this[EventosTable.estabelecimento]?.value,
        estabelecimentoNome = this.getOrNull(EstabelecimentosTable.nomeFantasia)
    )
}