package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.CupomUsuarioDTO
import com.example.projetorole.backend.models.CuponsUsuario
import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.models.EventosTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object CupomUsuarioService {

    private val cupomJoinEvento = CuponsUsuario
        .leftJoin(EventosTable, { eventoId }, { EventosTable.id })
        .leftJoin(EstabelecimentosTable, { EventosTable.estabelecimento }, { EstabelecimentosTable.id })

    fun listarCuponsDoUsuario(userId: Int): List<CupomUsuarioDTO> = transaction {
        cupomJoinEvento
            .select { CuponsUsuario.userId eq userId }
            .map { it.toCupomUsuarioDTO() }
    }

    fun marcarComoUsado(cupomId: Int, userId: Int): Boolean = transaction {

        CuponsUsuario.update({
            (CuponsUsuario.id eq cupomId) and
            (CuponsUsuario.userId eq userId) and
            (CuponsUsuario.usado eq false)
        }) {
            it[usado] = true
        } > 0
    }

    fun deletarCupom(cupomId: Int, userId: Int): Boolean = transaction {
        CuponsUsuario.deleteWhere {
            (CuponsUsuario.id eq cupomId) and
            (CuponsUsuario.userId eq userId)
        } > 0
    }

    private fun ResultRow.toCupomUsuarioDTO() = CupomUsuarioDTO(
        id = this[CuponsUsuario.id].value,
        eventoId = this[CuponsUsuario.eventoId].value,
        titulo = this[CuponsUsuario.titulo],
        descricao = this[CuponsUsuario.descricao],
        estabelecimentoNome = this.getOrNull(EstabelecimentosTable.nomeFantasia),
        usado = this[CuponsUsuario.usado],
        dataResgate = this[CuponsUsuario.dataResgate].toString()
    )
}