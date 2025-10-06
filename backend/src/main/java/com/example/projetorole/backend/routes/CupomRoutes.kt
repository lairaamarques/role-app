package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.CupomResponse
import com.example.projetorole.backend.models.CuponsTable
import com.example.projetorole.backend.security.ensureAuthenticated
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.configureCupomRoutes() {
    route("/api/cupons") {

        get {
            if (call.ensureAuthenticated() == null) return@get
            val cupons = transaction {
                CuponsTable.selectAll().map { row ->
                    CupomResponse(
                        id = row[CuponsTable.id].value,
                        titulo = row[CuponsTable.titulo],
                        descricao = row[CuponsTable.descricao],
                        local = row[CuponsTable.local],
                        disponivel = row[CuponsTable.disponivel]
                    )
                }
            }
            call.respond(ApiResponse(success = true, message = "Cupons carregados", data = cupons))
        }

        post {
            if (call.ensureAuthenticated() == null) return@post
            call.respond(HttpStatusCode.NotImplemented, ApiResponse<Unit>(false, "Ainda não implementado"))
        }

        put("/{id}") {
            if (call.ensureAuthenticated() == null) return@put
            call.respond(HttpStatusCode.NotImplemented, ApiResponse<Unit>(false, "Ainda não implementado"))
        }

        delete("/{id}") {
            if (call.ensureAuthenticated() == null) return@delete
            call.respond(HttpStatusCode.NotImplemented, ApiResponse<Unit>(false, "Ainda não implementado"))
        }
    }
}