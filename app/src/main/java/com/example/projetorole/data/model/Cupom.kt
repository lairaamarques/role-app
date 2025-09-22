package com.example.projetorole.data.model

data class Cupom(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val local: String,
    val disponivel: Boolean = true,
    val usado: Boolean = false,
    val dataExpiracao: String? = null
)