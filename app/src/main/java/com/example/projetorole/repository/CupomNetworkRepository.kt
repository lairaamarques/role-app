package com.example.projetorole.repository

import com.example.projetorole.data.model.Cupom
import com.example.projetorole.data.repository.CupomRepository
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.CupomNetwork
import com.example.projetorole.network.CupomRequest
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.postRemote
import com.example.projetorole.network.safeRemoteCall
import com.example.projetorole.network.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.example.projetorole.network.*

class CupomNetworkRepository : CupomRepository {

    override val cupons: Flow<List<Cupom>> = flow {
        emit(fetchCupons())
    }.catch { emit(emptyList()) }

    override suspend fun getCupons(): List<Cupom> =
        runCatching { fetchCupons() }.getOrElse { emptyList() }

    private suspend fun fetchCupons(): List<Cupom> = safeRemoteCall {
        val response: ApiResponse<List<CupomNetwork>> = getRemote("/api/cupons")
        if (response.success && response.data != null) {
            response.data.map { it.toModel() }
        } else {
            throw IllegalStateException(response.message)
        }
    }

    suspend fun criarCupom(titulo: String, descricao: String): Boolean {
        return try {
            safeRemoteCall {
                val request = CupomRequest(
                    titulo = titulo,
                    descricao = descricao,
                    local = "No Local",
                    disponivel = true
                )
                val response: ApiResponse<CupomNetwork> = postRemote("/api/cupons", request)
                response.success
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deletarCupom(cupomId: Int): Boolean {
        return try {
            safeRemoteCall {
                val response: ApiResponse<Unit> = com.example.projetorole.network.deleteRemote("/api/cupons/$cupomId")
                response.success
            }
        } catch (e: Exception) {
            false
        }
    }
}