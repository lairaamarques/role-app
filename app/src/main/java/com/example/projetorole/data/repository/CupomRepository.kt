package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Cupom
import kotlinx.coroutines.flow.Flow

interface CupomRepository {
    val cupons: Flow<List<Cupom>>
    suspend fun getCupons(): List<Cupom>
}