package com.example.projetorole.backend.database

import com.example.projetorole.backend.models.CuponsTable
import com.example.projetorole.backend.models.EventosTable
import com.example.projetorole.backend.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val dataSource = hikari()
        Database.connect(dataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                EventosTable,
                CuponsTable,
                Users
            )
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