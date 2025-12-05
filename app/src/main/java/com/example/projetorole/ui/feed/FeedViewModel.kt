package com.example.projetorole.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.data.model.Evento
import com.example.projetorole.data.repository.EventoRepository
import com.example.projetorole.repository.EventoNetworkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FeedViewModel(
    private val repository: EventoRepository = EventoNetworkRepository()
) : ViewModel() {

    val eventos: StateFlow<List<Evento>> = repository.eventos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}