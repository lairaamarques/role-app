package com.example.projetorole.backend.database

import com.example.projetorole.backend.models.CuponsTable
import com.example.projetorole.backend.models.EstabelecimentosTable
import com.example.projetorole.backend.models.EventosTable
import com.example.projetorole.backend.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object DatabaseFactory {

    fun init() {
        val dataSource = hikari()
        Database.connect(dataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                EstabelecimentosTable,
                EventosTable,
                CuponsTable,
                Users
            )
        }
        seedDefaultEstabelecimento()
    }

    private fun seedDefaultEstabelecimento() = transaction {
        val existe = EstabelecimentosTable.selectAll().limit(1).empty().not()
        if (!existe) {
            EstabelecimentosTable.insert {
                it[email] = "contato@rolestore.com"
                it[senhaHash] = BCrypt.hashpw("123456", BCrypt.gensalt(10))
                it[nomeFantasia] = "Rolê Store"
                it[cnpj] = null
                it[fotoUrl] = null
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:file:./data/projetorole;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}