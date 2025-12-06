package com.example.projetorole.data.model

data class Cupom(
    val id: Int,
    val eventoId: Int,
    val titulo: String,
    val descricao: String,
    val estabelecimentoNome: String? = null,
    val usado: Boolean,
    val dataResgate: String
)