package com.example.projetorole.ui.manage

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.example.projetorole.network.ApiClient
import com.example.projetorole.network.EventoUpsertRequestNetwork
import com.example.projetorole.repository.EstabelecimentoEventosRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import java.text.SimpleDateFormat
import java.util.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventFormUiState(
    val nome: String = "",
    val local: String = "",
    val data: String = "",
    val horario: String = "",
    val descricao: String = "",
    val pago: Boolean = false,
    val preco: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLocationSet: Boolean = false,
    val temCupom: Boolean = false,
    val cupomTitulo: String = "",
    val cupomDescricao: String = "",
    val cupomCheckinsNecessarios: String = "1",
    val paymentLink: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

private val horarioIsoRegex = Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}\$")
private val horarioRegexBrasileiro = Regex("^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}\$")

class EventFormViewModel(
    private val repository: EstabelecimentoEventosRepository = EstabelecimentoEventosRepository()
) : ViewModel() {

    companion object {
        private const val MAX_CUPOM_TITULO_CHARS = 40
        private const val MAX_CUPOM_DESCRICAO_CHARS = 80
    }

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
                            isEdit = true,
                            isLocationSet = true,
                            temCupom = !evento.cupomTitulo.isNullOrBlank(),
                            cupomTitulo = evento.cupomTitulo ?: "",
                            cupomDescricao = evento.cupomDescricao ?: "",
                            cupomCheckinsNecessarios = evento.cupomCheckinsNecessarios.coerceAtLeast(1).toString()
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
    fun onTemCupomChange(value: Boolean) {
        _uiState.value = if (value) {
            _uiState.value.copy(temCupom = true)
        } else {
            _uiState.value.copy(
                temCupom = false,
                cupomTitulo = "",
                cupomDescricao = "",
                cupomCheckinsNecessarios = "1"
            )
        }
    }
    fun onCupomTituloChange(value: String) {
        _uiState.value = _uiState.value.copy(
            cupomTitulo = value.take(MAX_CUPOM_TITULO_CHARS)
        )
    }

    fun onCupomDescricaoChange(value: String) {
        _uiState.value = _uiState.value.copy(
            cupomDescricao = value.take(MAX_CUPOM_DESCRICAO_CHARS)
        )
    }

    fun onCupomCheckinsChange(value: String) {
        val sanitized = value.filter { it.isDigit() }.take(3)
        _uiState.value = _uiState.value.copy(cupomCheckinsNecessarios = sanitized.ifBlank { "1" })
    }

    fun onLocationChange(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                isLocationSet = true
            )
        }
    }

    fun onPaymentLinkChange(value: String) { _uiState.value = _uiState.value.copy(paymentLink = value) }
    fun onImageUrlChange(value: String) { _uiState.value = _uiState.value.copy(imageUrl = value) }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
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

        val cupomTitulo = if (state.temCupom) state.cupomTitulo.trim().takeIf { it.isNotBlank() }?.take(MAX_CUPOM_TITULO_CHARS) else null
        val cupomDescricao = if (state.temCupom) state.cupomDescricao.trim().takeIf { it.isNotBlank() }?.take(MAX_CUPOM_DESCRICAO_CHARS) else null

        val cupomCheckinsInt: Int = if (state.temCupom) {
            state.cupomCheckinsNecessarios.trim().toIntOrNull()?.takeIf { it > 0 } ?: 1
        } else {
            1
        }

        if (state.temCupom) {
            if (cupomTitulo.isNullOrBlank()) {
                _uiState.value = state.copy(error = "Título do cupom obrigatório")
                return
            }
            if (cupomDescricao.isNullOrBlank()) {
                _uiState.value = state.copy(error = "Descrição do cupom obrigatória")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true, error = null, success = false)

        val request = EventoUpsertRequestNetwork(
            nome = trimmedNome,
            local = trimmedLocal,
            horario = isoHorario,
            pago = state.pago,
            preco = preco,
            descricao = state.descricao.takeIf { it.isNotBlank() },
            latitude = state.latitude,
            longitude = state.longitude,
            cupomTitulo = cupomTitulo,
            cupomDescricao = cupomDescricao,
            cupomCheckinsNecessarios = cupomCheckinsInt,
            paymentLink = state.paymentLink.takeIf { it.isNotBlank() },
            imageUrl = state.imageUrl.takeIf { it.isNotBlank() }
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

    suspend fun submitWithImage(context: Context, imageUri: Uri?) {
        val state = _uiState.value

        val dataTexto = state.data.trim()
        val horarioTexto = state.horario.trim()
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

        _uiState.value = state.copy(isLoading = true, error = null, success = false)

        try {
            val form = formData {
                append("nome", state.nome.trim())
                append("local", state.local.trim())
                append("horario", isoHorario)
                append("pago", state.pago.toString())
                if (state.pago && state.preco.isNotBlank()) append("preco", state.preco.trim())
                if (state.descricao.isNotBlank()) append("descricao", state.descricao)
                append("latitude", state.latitude.toString())
                append("longitude", state.longitude.toString())

                if (state.temCupom) {
                    if (state.cupomTitulo.isNotBlank()) append("cupomTitulo", state.cupomTitulo.trim())
                    if (state.cupomDescricao.isNotBlank()) append("cupomDescricao", state.cupomDescricao.trim())
                    append("cupomCheckinsNecessarios", (state.cupomCheckinsNecessarios.trim().toIntOrNull() ?: 1).toString())
                }

                if (state.paymentLink.isNotBlank()) append("paymentLink", state.paymentLink.trim())
                if (state.imageUrl.isNotBlank()) append("imageUrl", state.imageUrl.trim())

                if (imageUri != null) {
                    context.contentResolver.openInputStream(imageUri)?.use { input ->
                        val bytes = input.readBytes()
                        append(
                            key = "image",
                            bytes,
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"event.jpg\"")
                                append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            }
                        )
                    }
                }
            }

            val response = ApiClient.client.post("${ApiClient.BASE_URL}/api/eventos") {
                setBody(MultiPartFormDataContent(form))
            }.body<com.example.projetorole.network.ApiResponse<com.example.projetorole.network.EventoNetwork>>()

            if (response.success) {
                _uiState.value = state.copy(isLoading = false, success = true)
            } else {
                _uiState.value = state.copy(isLoading = false, error = response.message ?: "Erro ao criar evento")
            }
        } catch (e: Exception) {
            _uiState.value = state.copy(isLoading = false, error = e.message ?: "Erro de comunicação")
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}