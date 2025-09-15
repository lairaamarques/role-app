package com.example.projetorole.data.model

data class Evento(
    val id: Int,
    val nome: String,
    val horario: String,
    val local: String,
    val pago: Boolean,
    val checkIns: Int,
    val nota: Double? = null
)