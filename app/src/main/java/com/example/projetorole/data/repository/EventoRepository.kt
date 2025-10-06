package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Evento
import kotlinx.coroutines.flow.Flow

interface EventoRepository {
    val eventos: Flow<List<Evento>>
    suspend fun getEventos(): List<Evento>
}