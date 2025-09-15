package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Evento

class EventoRepositoryMock {
    fun getEventos(): List<Evento> = listOf(
        Evento(1, "Festa Universitária", "20:00", "Bar do Zé", true, 120, 4.5),
        Evento(2, "Show de Rock", "22:00", "Arena Manaus", false, 300, 4.8),
        Evento(3, "Noite Cultural", "19:00", "Teatro Amazonas", false, 80, null),
        Evento(4, "Samba no Largo", "18:00", "Largo São Sebastião", false, 200, 4.2)
    )
}