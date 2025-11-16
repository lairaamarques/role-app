package com.example.projetorole.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetorole.network.EventoUpsertRequestNetwork
import com.example.projetorole.repository.EstabelecimentoEventosRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

data class EventFormUiState(
    val nome: String = "",
    val local: String = "",
    val data: String = "",
    val horario: String = "", 
    val pago: Boolean = false,
    val preco: String = "",
    val descricao: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLocationSet: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isEdit: Boolean = false

)

private val horarioIsoRegex = Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}\$")
private val horarioRegexBrasileiro = Regex("^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}\$")  // Adicione no topo da classe

class EventFormViewModel(
    private val repository: EstabelecimentoEventosRepository = EstabelecimentoEventosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventFormUiState())
    val uiState: StateFlow<EventFormUiState> = _uiState.asStateFlow()

    private var editingId: Int? = null

    fun loadEvento(id: Int) {
        if (editingId == id && _uiState.value.isEdit) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { repository.buscarEvento(id) }
                .onSuccess { evento ->
                    if (evento != null) {
                        editingId = id
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                        val date = sdf.parse(evento.horario)
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val data = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                        val horario = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}"
                        _uiState.value = EventFormUiState(
                            nome = evento.nome,
                            local = evento.local,
                            data = data,
                            horario = horario,
                            pago = evento.pago,
                            preco = evento.preco?.toString() ?: "",
                            descricao = evento.descricao ?: "",
                            isEdit = true
                        )
                    } else {
                        _uiState.value = EventFormUiState(error = "Evento não encontrado")
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = EventFormUiState(error = throwable.message ?: "Erro ao carregar o evento")
                }
        }
    }

    fun onNomeChange(value: String) { _uiState.value = _uiState.value.copy(nome = value) }
    fun onLocalChange(value: String) { _uiState.value = _uiState.value.copy(local = value) }
    fun onDataChange(value: String) { _uiState.value = _uiState.value.copy(data = value) }
    fun onHorarioChange(value: String) { _uiState.value = _uiState.value.copy(horario = value) }
    fun onPagoChange(value: Boolean) { _uiState.value = _uiState.value.copy(pago = value) }
    fun onPrecoChange(value: String) { _uiState.value = _uiState.value.copy(preco = value) }
    fun onDescricaoChange(value: String) { _uiState.value = _uiState.value.copy(descricao = value) }

    fun onLocationChange(lat: Double, lon: Double) {
        _uiState.value = _uiState.value.copy(
            latitude = lat,
            longitude = lon,
            isLocationSet = true
        )
    }

    suspend fun submit() {
        val state = _uiState.value
        val trimmedNome = state.nome.trim()
        val trimmedLocal = state.local.trim()
        val dataTexto = state.data.trim()
        val horarioTexto = state.horario.trim()
        val precoTexto = state.preco.trim()

        if (!state.isEdit && !state.isLocationSet) {
            _uiState.value = state.copy(error = "Por favor, defina a localização do evento")
            return
        }

        if (dataTexto.isBlank() || horarioTexto.isBlank()) {
            _uiState.value = state.copy(error = "Preencha data e horário")
            return
        }

        if (!horarioRegexBrasileiro.matches("$dataTexto $horarioTexto")) {
            _uiState.value = state.copy(error = "Formato inválido (dd/MM/yyyy HH:mm)")
            return
        }

        val isoHorario = try {
            val parts = dataTexto.split("/")
            val timeParts = horarioTexto.split(":")
            "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}T${timeParts[0].padStart(2, '0')}:${timeParts[1].padStart(2, '0')}"
        } catch (e: Exception) {
            _uiState.value = state.copy(error = "Formato de data/horário inválido")
            return
        }

        val preco = if (state.pago && precoTexto.isNotBlank()) precoTexto.toDoubleOrNull() else null
        if (state.pago && precoTexto.isNotBlank() && preco == null) {
            _uiState.value = state.copy(error = "Preço inválido")
            return
        }
        val request = EventoUpsertRequestNetwork(
            nome = trimmedNome,
            local = trimmedLocal,
            horario = isoHorario,
            pago = state.pago,
            preco = preco,
            descricao = state.descricao.takeIf { it.isNotBlank() },
            latitude = state.latitude,
            longitude = state.longitude
        )

        val result = if (editingId != null) {
            runCatching { repository.atualizarEvento(editingId!!, request) }
        } else {
            runCatching { repository.criarEvento(request) }
        }
        result.onSuccess {
            _uiState.value = _uiState.value.copy(isLoading = false, success = true)
        }.onFailure { throwable ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = throwable.message ?: "Não foi possível salvar o evento"
            )
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}