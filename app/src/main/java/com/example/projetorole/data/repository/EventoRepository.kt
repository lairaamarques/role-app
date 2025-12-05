package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Evento
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.CheckInDTO
import kotlinx.coroutines.flow.Flow

interface EventoRepository {
    val eventos: Flow<List<Evento>>
    suspend fun getEventos(): List<Evento>

    suspend fun checkIn(eventId: Int, latitude: Double, longitude: Double): ApiResponse<CheckInDTO>
}