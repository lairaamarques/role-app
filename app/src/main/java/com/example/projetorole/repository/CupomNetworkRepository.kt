package com.example.projetorole.repository

import com.example.projetorole.data.model.Cupom
import com.example.projetorole.data.repository.CupomRepository
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.postRemote
import com.example.projetorole.network.safeRemoteCall
import com.example.projetorole.network.toModel
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CupomNetworkRepository : CupomRepository {

    override suspend fun getCupons(): List<Cupom> {
        return try {
            safeRemoteCall {
                val response = getRemote<com.example.projetorole.network.ApiResponse<List<com.example.projetorole.network.CupomUsuarioNetwork>>>(
                    "/api/cupons"
                )

                if (response.success && response.data != null) {
                    response.data.map { net ->
                        Cupom(
                            id = net.id,
                            eventoId = net.eventoId,
                            titulo = net.titulo,
                            descricao = net.descricao ?: "",
                            estabelecimentoNome = net.estabelecimentoNome,
                            usado = net.usado,
                            dataResgate = net.dataResgate
                        )
                    }
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun usarCupom(cupomId: Int): Boolean {
        return try {
            safeRemoteCall {
                val response: ApiResponse<Unit> = postRemote("/api/cupons/$cupomId/usar", Unit)
                response.success
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteCupom(cupomId: Int): Boolean {
        return try {
            val resp = ApiClient.client.delete("${ApiClient.BASE_URL}/api/cupons/$cupomId").body<ApiResponse<Unit>>()
            resp.success
        } catch (e: Exception) {
            false
        }
    }
}