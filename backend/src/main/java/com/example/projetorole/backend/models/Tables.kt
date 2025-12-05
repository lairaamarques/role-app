package com.example.projetorole.backend.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

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
    val latitude = double("latitude").default(0.0)
    val longitude = double("longitude").default(0.0)
    val cupomTitulo = varchar("cupom_titulo", 255).nullable()
    val cupomDescricao = text("cupom_descricao").nullable()
    val cupomCheckinsNecessarios = integer("cupom_checkins_necessarios").default(1)
    val paymentLink = varchar("payment_link", 500).nullable()
    val imageUrl = varchar("image_url", 500).nullable() // new: optional image URL for event
}

object CheckIns : IntIdTable("check_ins"){
    val userId = reference("user_id", Users.id)
    val eventoId = reference("evento_id", EventosTable.id)
    val validatedAt = datetime("validated_at").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

object CuponsUsuario : IntIdTable("cupons_usuario") {
    val userId = reference("user_id", Users.id)
    val eventoId = reference("evento_id", EventosTable.id)
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val usado = bool("usado").default(false)
    val dataResgate = datetime("data_resgate").default(LocalDateTime.now())
}

