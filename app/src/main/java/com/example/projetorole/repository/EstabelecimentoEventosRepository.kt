package com.example.projetorole.repository

import com.example.projetorole.data.model.Evento
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.EventoNetwork
import com.example.projetorole.network.EventoUpsertRequestNetwork
import com.example.projetorole.network.deleteRemote
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.postRemote
import com.example.projetorole.network.putRemote
import com.example.projetorole.network.safeRemoteCall
import com.example.projetorole.network.toModel

class EstabelecimentoEventosRepository {

    suspend fun getMeusEventos(): List<Evento> = safeRemoteCall {
        val response: ApiResponse<List<EventoNetwork>> = getRemote("/api/estabelecimentos/me/eventos")
        if (response.success && response.data != null) response.data.map { it.toModel() }
        else throw IllegalStateException(response.message)
    }

    suspend fun criarEvento(request: EventoUpsertRequestNetwork): Evento = safeRemoteCall {
        val response: ApiResponse<EventoNetwork> = postRemote("/api/eventos", request)
        if (response.success && response.data != null) response.data.toModel()
        else throw IllegalStateException(response.message)
    }

    suspend fun atualizarEvento(id: Int, request: EventoUpsertRequestNetwork): Evento = safeRemoteCall {
        val response: ApiResponse<EventoNetwork> = putRemote("/api/eventos/$id", request)
        if (response.success && response.data != null) response.data.toModel()
        else throw IllegalStateException(response.message)
    }

    suspend fun deletarEvento(id: Int): Boolean = safeRemoteCall {
        val response: ApiResponse<Unit> = deleteRemote("/api/eventos/$id")
        response.success
    }

    suspend fun buscarEvento(id: Int): Evento? = safeRemoteCall {
        val response: ApiResponse<EventoNetwork> = getRemote("/api/eventos/$id")
        if (response.success && response.data != null) response.data.toModel() else null
    }
}