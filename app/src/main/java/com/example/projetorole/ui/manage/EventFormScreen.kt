package com.example.projetorole.ui.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    viewModel: EventFormViewModel,
    eventoId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventoId) {
        if (eventoId != null) viewModel.loadEvento(eventoId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.consumeSuccess()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text(if (uiState.isEdit) "Editar evento" else "Novo evento") },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Preencha os detalhes do evento",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = viewModel::onNomeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nome do evento", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                    colors = eventOutlinedColors(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.local,
                    onValueChange = viewModel::onLocalChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Local", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                    colors = eventOutlinedColors(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.data,
                    onValueChange = viewModel::onDataChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Data (dd/MM/yyyy)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                    colors = eventOutlinedColors(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.horario,
                    onValueChange = viewModel::onHorarioChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Horário (HH:mm)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                    colors = eventOutlinedColors(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.descricao,
                    onValueChange = viewModel::onDescricaoChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Descrição (opcional)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                    maxLines = 5,
                    colors = eventOutlinedColors(),
                    enabled = !uiState.isLoading
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.pago,
                        onCheckedChange = viewModel::onPagoChange,
                        enabled = !uiState.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFCC00),
                            uncheckedColor = Color.White,
                            checkmarkColor = Color(0xFF090040)
                        )
                    )
                    Text("Evento pago", color = Color.White, modifier = Modifier.padding(start = 8.dp))
                }

                if (uiState.pago) {
                    OutlinedTextField(
                        value = uiState.preco,
                        onValueChange = viewModel::onPrecoChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Preço (opcional)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                        colors = eventOutlinedColors(),
                        singleLine = true,
                        enabled = !uiState.isLoading
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.submit()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCC00),
                        contentColor = Color(0xFF090040)
                    )
                ) {
                    Text(if (uiState.isEdit) "Atualizar evento" else "Criar evento", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun eventOutlinedColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White.copy(alpha = 0.6f),
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White.copy(alpha = 0.4f),
    disabledIndicatorColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
    disabledLabelColor = Color.White.copy(alpha = 0.5f),
    focusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    unfocusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    focusedTextColor = Color(0xFF090040),
    unfocusedTextColor = Color(0xFF090040),
    disabledTextColor = Color(0xFF090040).copy(alpha = 0.5f),
    cursorColor = Color(0xFF090040)
)