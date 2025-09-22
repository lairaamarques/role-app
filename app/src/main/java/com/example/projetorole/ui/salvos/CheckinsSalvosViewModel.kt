package com.example.projetorole.ui.salvos

import androidx.lifecycle.ViewModel
import com.example.projetorole.data.repository.CheckinsSalvosRepository
import com.example.projetorole.data.model.CheckinSalvo
import kotlinx.coroutines.flow.StateFlow

class CheckinsSalvosViewModel : ViewModel() {
    private val repository = CheckinsSalvosRepository()
    
    val checkinsSalvos: StateFlow<List<CheckinSalvo>> = repository.checkinsSalvos
    
    fun toggleSalvarCheckin(eventoId: Int) {
        if (repository.isCheckinSalvo(eventoId)) {
            repository.removerCheckinSalvo(eventoId)
        } else {
            repository.salvarCheckin(eventoId)
        }
    }
    
    fun isCheckinSalvo(eventoId: Int): Boolean {
        return repository.isCheckinSalvo(eventoId)
    }
    
    fun salvarCheckin(eventoId: Int) {
        repository.salvarCheckin(eventoId)
    }
    
    fun removerCheckinSalvo(eventoId: Int) {
        repository.removerCheckinSalvo(eventoId)
    }
}