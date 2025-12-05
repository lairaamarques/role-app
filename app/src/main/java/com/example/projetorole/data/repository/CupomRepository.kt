package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Cupom

interface CupomRepository {
    suspend fun getCupons(): List<Cupom>
    suspend fun usarCupom(cupomId: Int): Boolean
    suspend fun deleteCupom(cupomId: Int): Boolean
}