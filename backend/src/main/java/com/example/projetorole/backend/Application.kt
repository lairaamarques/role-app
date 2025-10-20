package com.example.projetorole.backend

import com.example.projetorole.backend.database.DatabaseFactory
import com.example.projetorole.backend.routes.configureAuthRoutes
import com.example.projetorole.backend.routes.configureCupomRoutes
import com.example.projetorole.backend.routes.configureEstabelecimentoAuthRoutes
import com.example.projetorole.backend.routes.configureEventoRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    routing {
        get("/") {
            call.respondText("ProjetoRole Backend está funcionando!")
        }
        get("/api/health") {
            call.respond(
                mapOf(
                    "status" to "OK",
                    "message" to "Backend funcionando!",
                    "timestamp" to System.currentTimeMillis()
                )
            )
        }

        configureAuthRoutes()
        configureEstabelecimentoAuthRoutes()
        configureEventoRoutes()
        configureCupomRoutes()
    }
}