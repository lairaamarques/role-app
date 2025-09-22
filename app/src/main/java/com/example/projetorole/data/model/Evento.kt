package com.example.projetorole.data.model

data class Evento(
    val id: Int,
    val nome: String,
    val horario: String,
    val local: String,
    val checkIns: Int,
    val nota: Double? = null,
    val pago: Boolean = true, // ← ADICIONAR ESTE CAMPO
    val preco: Double? = null // ← ADICIONAR ESTE CAMPO
) {
    fun getPrecoFormatado(): String {
        return if (pago && preco != null) {
            "R$ ${String.format("%.0f", preco)}"
        } else {
            "Gratuito"
        }
    }
}