package com.example.projetorole.data.model

data class CheckinSalvo(
    val eventoId: Int,
    val dataSalvo: Long = System.currentTimeMillis()
)