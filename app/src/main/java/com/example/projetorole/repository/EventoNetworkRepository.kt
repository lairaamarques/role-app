package com.example.projetorole.repository

import com.example.projetorole.data.model.Evento
import com.example.projetorole.data.repository.EventoRepository
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.EventoNetwork
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.safeRemoteCall
import com.example.projetorole.network.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.example.projetorole.network.CheckInDTO
import com.example.projetorole.network.CheckInRequest
import com.example.projetorole.network.postRemote

class EventoNetworkRepository : EventoRepository {

    override val eventos: Flow<List<Evento>> = flow {
        emit(fetchEventos())
    }.catch { emit(emptyList()) }

    override suspend fun getEventos(): List<Evento> =
        runCatching { fetchEventos() }.getOrElse { emptyList() }

    private suspend fun fetchEventos(): List<Evento> = safeRemoteCall {
        val response: ApiResponse<List<EventoNetwork>> = getRemote("/api/eventos")
        if (response.success && response.data != null) {
            response.data.map { it.toModel() }
        } else {
            throw IllegalStateException(response.message)
        }
    }

    override suspend fun checkIn(
        eventId: Int,
        latitude: Double,
        longitude: Double
    ): ApiResponse<CheckInDTO> {

        return safeRemoteCall {

            val requestBody = CheckInRequest(
                latitude = latitude,
                longitude = longitude
            )

            val path = "/api/eventos/$eventId/checkin"

            postRemote(path, requestBody)
        }
    }

}