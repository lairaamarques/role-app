package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Cupom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CupomRepositoryMock {
    private val _cupons = MutableStateFlow(
        listOf(
            Cupom(
                id = 1,
                titulo = "2 por 1: Caipirinha em dobro !",
                descricao = "Compre uma caipirinha e ganhe outra grátis",
                local = "Cabocos Bar",
                disponivel = true
            ),
            Cupom(
                id = 2,
                titulo = "R$ 10 em Petiscos",
                descricao = "Desconto de R$ 10 em qualquer petisco",
                local = "Cabocos Bar",
                disponivel = true
            ),
            Cupom(
                id = 3,
                titulo = "Entrada gratuita",
                descricao = "Entrada gratuita em qualquer evento",
                local = "Rock Bar",
                disponivel = true,
            )
        )
    )
    
    val cupons: StateFlow<List<Cupom>> = _cupons.asStateFlow()
    
    fun usarCupom(cupomId: Int) {
        val cuponsAtuais = _cupons.value.toMutableList()
        val index = cuponsAtuais.indexOfFirst { it.id == cupomId }
        if (index != -1) {
            cuponsAtuais[index] = cuponsAtuais[index].copy(usado = true, disponivel = false)
            _cupons.value = cuponsAtuais
        }
    }
}