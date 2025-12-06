package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.models.CuponsUsuario
import com.example.projetorole.backend.models.CupomUsuarioDTO
import com.example.projetorole.backend.models.EventoRequest
import com.example.projetorole.backend.models.EventoResponse
import com.example.projetorole.backend.models.EventosTable
import com.example.projetorole.backend.models.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.projetorole.backend.models.CheckInDTO
import com.example.projetorole.backend.models.CheckIns
import java.time.LocalDateTime

class EventNotFoundException : Exception("Evento não encontrado.")
class DistanceCheckException : Exception("Usuário muito longe do evento.")
class DuplicateCheckInException : Exception("Check-in já realizado por este usuário.")

object EventoService {

    private const val MAX_DISTANCE_IN_KILOMETERS = 0.1
    private const val MAX_CUPOM_TITULO_CHARS = 40
    private const val MAX_CUPOM_DESCRICAO_CHARS = 80

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
        val cupomTituloSanitizado = request.cupomTitulo?.trim()?.take(MAX_CUPOM_TITULO_CHARS)
        val cupomDescricaoSanitizado = request.cupomDescricao?.trim()?.take(MAX_CUPOM_DESCRICAO_CHARS)
        val cupomCheckins = request.cupomCheckinsNecessarios.coerceAtLeast(1)
        val paymentLinkSanitizado = request.paymentLink?.trim()?.takeIf { it.isNotBlank() }
        val imageUrlSanitizado = request.imageUrl?.trim()?.takeIf { it.isNotBlank() }

        val insertedId = EventosTable.insert {
            it[nome] = nomeSanitizado
            it[local] = localSanitizado
            it[horario] = horarioSanitizado
            it[pago] = request.pago
            it[preco] = request.preco
            it[descricao] = request.descricao
            it[cupomCheckinsNecessarios] = cupomCheckins
            it[estabelecimento] = EntityID(establishmentId, EstabelecimentosTable)
            it[latitude] = request.latitude
            it[longitude] = request.longitude
            it[cupomTitulo] = cupomTituloSanitizado
            it[cupomDescricao] = cupomDescricaoSanitizado
            it[paymentLink] = paymentLinkSanitizado
            it[imageUrl] = imageUrlSanitizado
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
        val row = EventosTable.select { EventosTable.id eq id }.singleOrNull()
            ?: return@transaction UpdateResult.NotFound

        val ownerId = row[EventosTable.estabelecimento]?.value
        if (ownerId == null || ownerId != establishmentId) return@transaction UpdateResult.Forbidden

        val paymentLinkSanitizado = request.paymentLink?.trim()?.takeIf { it.isNotBlank() }

        EventosTable.update({ EventosTable.id eq id }) {
            it[nome] = request.nome.trim()
            it[local] = request.local.trim()
            it[horario] = request.horario.trim()
            it[pago] = request.pago
            it[preco] = request.preco
            it[descricao] = request.descricao
            it[latitude] = request.latitude
            it[longitude] = request.longitude
            it[cupomTitulo] = request.cupomTitulo?.trim()?.take(MAX_CUPOM_TITULO_CHARS)
            it[cupomDescricao] = request.cupomDescricao?.trim()?.take(MAX_CUPOM_DESCRICAO_CHARS)
            it[cupomCheckinsNecessarios] = request.cupomCheckinsNecessarios.coerceAtLeast(1)
            it[paymentLink] = paymentLinkSanitizado
        }

        val updatedRow = eventoJoinEstabelecimento.select { EventosTable.id eq id }.single()
        UpdateResult.Success(updatedRow.toEventoResponse())
    }

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data object NotFound : DeleteResult()
        data object Forbidden : DeleteResult()
    }

    fun removerEvento(id: Int, establishmentId: Int): DeleteResult = transaction {
        val row = EventosTable.select { EventosTable.id eq id }.singleOrNull()
            ?: return@transaction DeleteResult.NotFound

        val ownerId = row[EventosTable.estabelecimento]?.value
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
        val eventRow = eventoJoinEstabelecimento
            .select { EventosTable.id eq eventId }
            .singleOrNull() ?: throw EventNotFoundException()

        val existing = CheckIns.select {
            (CheckIns.userId eq EntityID(userIdParam, Users)) and
            (CheckIns.eventoId eq EntityID(eventId, EventosTable))
        }.singleOrNull()

        if (existing != null) {
            throw DuplicateCheckInException()
        }

        val eventLatitude = eventRow[EventosTable.latitude]
        val eventLongitude = eventRow[EventosTable.longitude]

        val distance = DistanceCalculator.calculate(
            userLatitude, userLongitude, eventLatitude, eventLongitude
        )

        if (distance > MAX_DISTANCE_IN_KILOMETERS) throw DistanceCheckException()

        val insertId = CheckIns.insert {
            it[userId] = EntityID(userIdParam, Users)
            it[eventoId] = EntityID(eventId, EventosTable)
            it[validatedAt] = LocalDateTime.now()
        } get CheckIns.id

        val novoTotal = eventRow[EventosTable.checkIns] + 1
        EventosTable.update({ EventosTable.id eq eventId }) {
            it[checkIns] = novoTotal
        }

        val cupomGanho = verificarEConcederCupom(userIdParam, eventId, eventRow)

        val created = CheckIns.select { CheckIns.id eq insertId }.single()

        CheckInDTO(
            id = created[CheckIns.id].value,
            userId = created[CheckIns.userId].value,
            eventoId = created[CheckIns.eventoId].value,
            validatedAt = created[CheckIns.validatedAt]?.toString(),
            createdAt = created[CheckIns.createdAt].toString(),
            cupomGanho = cupomGanho
        )
    }

    fun listarEventosDoEstabelecimento(establishmentId: Int): List<EventoResponse> = transaction {
        val entity = EntityID(establishmentId, EstabelecimentosTable)
        eventoJoinEstabelecimento
            .select { EventosTable.estabelecimento eq entity }
            .map { it.toEventoResponse() }
    }

    private fun verificarEConcederCupom(
        userId: Int,
        eventId: Int,
        event: ResultRow
    ): CupomUsuarioDTO? {
        val titulo = event[EventosTable.cupomTitulo] ?: return null
        val descricao = event[EventosTable.cupomDescricao] ?: return null
        val checkinsNecessarios = event[EventosTable.cupomCheckinsNecessarios].coerceAtLeast(1)

        val jaGanhou = CuponsUsuario.select {
            (CuponsUsuario.userId eq EntityID(userId, Users)) and (CuponsUsuario.eventoId eq EntityID(eventId, EventosTable))
        }.singleOrNull() != null

        if (jaGanhou) return null

        val totalCheckinsUsuario = CheckIns.select { CheckIns.userId eq EntityID(userId, Users) }.count()

        if (totalCheckinsUsuario < checkinsNecessarios) return null

        val insertId = CuponsUsuario.insert {
            it[CuponsUsuario.userId] = EntityID(userId, Users)
            it[CuponsUsuario.eventoId] = EntityID(eventId, EventosTable)
            it[CuponsUsuario.titulo] = titulo
            it[CuponsUsuario.descricao] = descricao
            it[CuponsUsuario.usado] = false
            it[CuponsUsuario.dataResgate] = LocalDateTime.now()
        } get CuponsUsuario.id

        val estabelecimentoNome = event.getOrNull(EstabelecimentosTable.nomeFantasia)

        return CupomUsuarioDTO(
            id = insertId.value,
            eventoId = eventId,
            titulo = titulo,
            descricao = descricao,
            estabelecimentoNome = estabelecimentoNome,
            usado = false,
            dataResgate = LocalDateTime.now().toString()
        )
    }

    private fun ResultRow.toEventoResponse(checkInsOverride: Int? = null) = EventoResponse(
        id = this[EventosTable.id].value,
        nome = this[EventosTable.nome],
        local = this[EventosTable.local],
        horario = this[EventosTable.horario],
        checkIns = checkInsOverride ?: this[EventosTable.checkIns],
        paid = this[EventosTable.pago],
        preco = this[EventosTable.preco],
        descricao = this[EventosTable.descricao],
        estabelecimentoId = this[EventosTable.estabelecimento]?.value,
        estabelecimentoNome = this.getOrNull(EstabelecimentosTable.nomeFantasia),
        cupomTitulo = this[EventosTable.cupomTitulo],
        cupomDescricao = this[EventosTable.cupomDescricao],
        cupomCheckinsNecessarios = this[EventosTable.cupomCheckinsNecessarios],
        paymentLink = this[EventosTable.paymentLink],
        imageUrl = this[EventosTable.imageUrl]
    )


}