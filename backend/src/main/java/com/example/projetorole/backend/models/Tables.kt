package com.example.projetorole.backend.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Users : IntIdTable("usuarios") {
    val email = varchar("email", 150).uniqueIndex()
    val senhaHash = varchar("senha_hash", 60)
    val nome = varchar("nome", 120).nullable()
    val fotoUrl = varchar("foto_url", 500).nullable()
}

object EstabelecimentosTable : IntIdTable("estabelecimentos") {
    val email = varchar("email", 150).uniqueIndex()
    val senhaHash = varchar("senha_hash", 60)
    val nomeFantasia = varchar("nome_fantasia", 150)
    val cnpj = varchar("cnpj", 20).nullable()
    val fotoUrl = varchar("foto_url", 500).nullable()
}

object EventosTable : IntIdTable("eventos") {
    val nome = varchar("nome", 255)
    val local = varchar("local", 255)
    val horario = varchar("horario", 64)
    val checkIns = integer("check_ins").default(0)
    val pago = bool("pago").default(false)
    val preco = double("preco").nullable()
    val descricao = text("descricao").nullable()
    val estabelecimento = reference(
        name = "estabelecimento_id",
        foreign = EstabelecimentosTable,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
}

object CuponsTable : IntIdTable("cupons") {
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val local = varchar("local", 255)
    val disponivel = bool("disponivel").default(true)
}