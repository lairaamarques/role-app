package com.example.projetorole.data.repository

import com.example.projetorole.data.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventoRepositoryMock {
    private val _eventos = MutableStateFlow(
        listOf(
            Evento(
                id = 1,
                nome = "Noite do Sertanejo",
                horario = "22h",
                local = "Rua dos Frades, 450 - Cabocos Bar",
                checkIns = 152,
                nota = null,
                pago = true,
                preco = 10.0
            ),
            Evento(
                id = 2,
                nome = "Rock Day",
                horario = "21h", 
                local = "Rua da Paz, 1000",
                checkIns = 50,
                nota = null,
                pago = true,
                preco = 15.0
            ),
            Evento(
                id = 3,
                nome = "Karaokê - A Noite Toda",
                horario = "21h",
                local = "Rua Andre Lima, 45",
                checkIns = 50,
                nota = null,
                pago = false,
                preco = null
            ),
            Evento(
                id = 4,
                nome = "Summer EletroHits",
                horario = "21h",
                local = "Avenida André Araújo, 50",
                checkIns = 50,
                nota = null,
                pago = true,
                preco = 15.0
            ),
            Evento(
                id = 5,
                nome = "Jazz Night",
                horario = "20h",
                local = "Centro Cultural, 123",
                checkIns = 35,
                nota = null,
                pago = true,
                preco = 25.0
            ),
            Evento(
                id = 6,
                nome = "Open Mic Night",
                horario = "19h",
                local = "Café da Esquina, 789",
                checkIns = 20,
                nota = null,
                pago = false,
                preco = null
            )
        )
    )
    
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()
    
    suspend fun getEventos(): List<Evento> {
        return _eventos.value
    }
}