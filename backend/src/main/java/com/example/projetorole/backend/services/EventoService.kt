package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.EventoRequest
import com.example.projetorole.backend.models.EventoResponse
import com.example.projetorole.backend.models.EventosTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object EventoService {

    fun listarEventos(): List<EventoResponse> = transaction {
        EventosTable.selectAll().map { it.toEventoResponse() }
    }

    fun buscarEvento(id: Int): EventoResponse? = transaction {
        EventosTable.select { EventosTable.id eq id }
            .singleOrNull()
            ?.toEventoResponse()
    }

    fun criarEvento(request: EventoRequest): EventoResponse = transaction {
        val insertedId = EventosTable.insert {
            it[nome] = request.nome
            it[local] = request.local
            it[horario] = request.horario
            it[pago] = request.pago
            it[preco] = request.preco
        } get EventosTable.id

        EventosTable.select { EventosTable.id eq insertedId.value }
            .single()
            .toEventoResponse()
    }

    fun atualizarEvento(id: Int, request: EventoRequest): EventoResponse? = transaction {
        val updated = EventosTable.update({ EventosTable.id eq id }) {
            it[nome] = request.nome
            it[local] = request.local
            it[horario] = request.horario
            it[pago] = request.pago
            it[preco] = request.preco
        }
        if (updated > 0) {
            EventosTable.select { EventosTable.id eq id }
                .single()
                .toEventoResponse()
        } else {
            null
        }
    }

    fun removerEvento(id: Int): Boolean = transaction {
        EventosTable.deleteWhere { EventosTable.id eq id } > 0
    }

    fun registrarCheckIn(id: Int): EventoResponse? = transaction {
        val row = EventosTable.select { EventosTable.id eq id }.singleOrNull() ?: return@transaction null
        val novoTotal = row[EventosTable.checkIns] + 1
        EventosTable.update({ EventosTable.id eq id }) {
            it[checkIns] = novoTotal
        }
        row.toEventoResponse(checkInsOverride = novoTotal)
    }

    private fun ResultRow.toEventoResponse(checkInsOverride: Int? = null) = EventoResponse(
        id = this[EventosTable.id].value,
        nome = this[EventosTable.nome],
        local = this[EventosTable.local],
        horario = this[EventosTable.horario],
        checkIns = checkInsOverride ?: this[EventosTable.checkIns],
        pago = this[EventosTable.pago],
        preco = this[EventosTable.preco]
    )
}