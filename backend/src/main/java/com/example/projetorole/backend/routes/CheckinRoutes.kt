package com.example.projetorole.backend.routes

import com.example.projetorole.backend.models.ApiResponse
import com.example.projetorole.backend.models.CheckInDTO
import com.example.projetorole.backend.models.CheckIns
import com.example.projetorole.backend.models.EventosTable
import com.example.projetorole.backend.models.Users
import com.example.projetorole.backend.security.ensureAuthenticated
import com.example.projetorole.backend.security.SubjectType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.configureCheckinRoutes() {
    get("/api/me/checkins") {
        val principal = call.ensureAuthenticated() ?: return@get
        if (principal.subjectType != SubjectType.USER) {
            call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(false, "Apenas usuários podem acessar seus check-ins"))
            return@get
        }

        val list = transaction {
            (CheckIns.leftJoin(EventosTable, { CheckIns.eventoId }, { EventosTable.id }))
                .select { CheckIns.userId eq principal.subjectId }
                .map { row -> row.toCheckInDTO() }
        }

        call.respond(ApiResponse(success = true, message = "Check‑ins carregados", data = list))
    }
}

private fun ResultRow.toCheckInDTO(): CheckInDTO {
    return CheckInDTO(
        id = this[CheckIns.id].value,
        userId = this[CheckIns.userId].value,
        eventoId = this[CheckIns.eventoId].value,
        validatedAt = this[CheckIns.validatedAt]?.toString(),
        createdAt = this[CheckIns.createdAt].toString(),
        cupomGanho = null
    )
}