package com.example.projetorole.backend.services

import com.example.projetorole.backend.models.CupomRequest
import com.example.projetorole.backend.models.CupomResponse
import com.example.projetorole.backend.models.CuponsTable
import com.example.projetorole.backend.models.EstabelecimentosTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object CupomService {
    private val cupomJoinEstabelecimento =
        CuponsTable.leftJoin(
            otherTable = EstabelecimentosTable,
            onColumn = { estabelecimento },
            otherColumn = { EstabelecimentosTable.id }
        )

    fun listarCupons(): List<CupomResponse> = transaction {
        cupomJoinEstabelecimento
            .selectAll()
            // .select { CuponsTable.disponivel eq true }
            .map { it.toCupomResponse() }
    }

    fun criarCupom(request: CupomRequest, establishmentId: Int): CupomResponse = transaction {
        val insertedId = CuponsTable.insert {
            it[titulo] = request.titulo
            it[descricao] = request.descricao
            it[local] = request.local
            it[disponivel] = request.disponivel
            it[estabelecimento] = EntityID(establishmentId, EstabelecimentosTable)
        } get CuponsTable.id

        return@transaction cupomJoinEstabelecimento
            .selectAll() // (select { CuponsTable.id eq insertedId }
            .where { CuponsTable.id eq insertedId }
            .single()
            .toCupomResponse()
    }

    fun deletarCupom(cupomId: Int, establishmentId: Int): Boolean = transaction {
        val deletedCount = CuponsTable.deleteWhere {
            (CuponsTable.id eq cupomId) and (CuponsTable.estabelecimento eq establishmentId)
        }
        deletedCount > 0
    }
    private fun ResultRow.toCupomResponse() = CupomResponse(
        id = this[CuponsTable.id].value,
        titulo = this[CuponsTable.titulo],
        descricao = this[CuponsTable.descricao],
        local = this[CuponsTable.local],
        disponivel = this[CuponsTable.disponivel],
        estabelecimentoId = this[CuponsTable.estabelecimento].value,
        estabelecimentoNome = this.getOrNull(EstabelecimentosTable.nomeFantasia)
    )
}