package com.example.projetorole.ui.manage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedImageUri by remember { androidx.compose.runtime.mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { it.value }) {
            getCurrentLocation(context, locationClient) { lat, lon ->
                viewModel.onLocationChange(lat, lon)
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permissão de localização é necessária.") }
        }
    }

    val requestLocation: () -> Unit = {
        if (hasLocationPermission(context)) {
            getCurrentLocation(context, locationClient) { lat, lon ->
                viewModel.onLocationChange(lat, lon)
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(eventoId) {
        if (eventoId != null) {
            viewModel.loadEvento(eventoId)
        } else {
            requestLocation()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        color = Color(0xFFFFCC00),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Preencha os detalhes do rolê",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = viewModel::onNomeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome do evento") },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    colors = eventOutlinedColors()
                )

                OutlinedTextField(
                    value = uiState.local,
                    onValueChange = viewModel::onLocalChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Local") },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    colors = eventOutlinedColors()
                )

                OutlinedTextField(
                    value = uiState.data,
                    onValueChange = viewModel::onDataChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Data (dd/MM/yyyy)") },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = eventOutlinedColors()
                )

                OutlinedTextField(
                    value = uiState.horario,
                    onValueChange = viewModel::onHorarioChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Horário (HH:mm)") },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = eventOutlinedColors()
                )

                OutlinedTextField(
                    value = uiState.descricao,
                    onValueChange = viewModel::onDescricaoChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    label = { Text("Descrição") },
                    enabled = !uiState.isLoading,
                    maxLines = 5,
                    colors = eventOutlinedColors()
                )

                OutlinedTextField(
                    value = uiState.paymentLink,
                    onValueChange = viewModel::onPaymentLinkChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Link de pagamento (ex: https://...)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    colors = eventOutlinedColors(),
                    enabled = !uiState.isLoading
                )

                Spacer(Modifier.height(6.dp))

                Text(text = "Imagem (opcional)", color = Color(0xFFFFFFFF))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { imagePicker.launch("image/*") }) { Text("Escolher imagem") }
                    if (selectedImageUri != null) {
                        AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.size(64.dp), contentScale = ContentScale.Crop)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.pago,
                        onCheckedChange = { viewModel.onPagoChange(it) },
                        enabled = !uiState.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFCC00),
                            uncheckedColor = Color.White
                        )
                    )
                    Text(
                        text = "Evento pago",
                        color = Color.White
                    )
                }

                if (uiState.pago) {
                    OutlinedTextField(
                        value = uiState.preco,
                        onValueChange = viewModel::onPrecoChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Preço (opcional)", color = Color.Black) },
                        colors = eventOutlinedColors()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { requestLocation() },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF090040))
                        Spacer(Modifier.width(8.dp))
                        Text("Atualizar localização", color = Color(0xFF090040))
                    }
                    Text(
                        text = if (uiState.isLocationSet) "Localização definida" else "Localização pendente",
                        color = if (uiState.isLocationSet) Color(0xFF4CAF50) else Color(0xFFFFCC00)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.temCupom,
                        onCheckedChange = viewModel::onTemCupomChange,
                        enabled = !uiState.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFCC00),
                            uncheckedColor = Color.White
                        )
                    )
                    Text(
                        text = "Oferecer cupom após check-ins",
                        color = Color.White
                    )
                }

                if (uiState.temCupom) {
                    OutlinedTextField(
                        value = uiState.cupomTitulo,
                        onValueChange = viewModel::onCupomTituloChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Título do cupom") },
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        colors = eventOutlinedColors()
                    )

                    OutlinedTextField(
                        value = uiState.cupomDescricao,
                        onValueChange = viewModel::onCupomDescricaoChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Descrição / Regras") },
                        enabled = !uiState.isLoading,
                        maxLines = 4,
                        colors = eventOutlinedColors()
                    )

                    OutlinedTextField(
                        value = uiState.cupomCheckinsNecessarios,
                        onValueChange = viewModel::onCupomCheckinsChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Check-ins necessários") },
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = eventOutlinedColors()
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.submitWithImage(context, selectedImageUri)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF090040))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isEdit) "Salvar alterações" else "Criar evento",
                        color = Color(0xFF090040),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    locationClient: FusedLocationProviderClient,
    onLocationFetched: (latitude: Double, longitude: Double) -> Unit
) {
    if (!hasLocationPermission(context)) return

    locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationFetched(location.latitude, location.longitude)
            } else {
                Toast.makeText(context, "Não foi possível obter a localização. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao obter localização: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

@Composable
private fun eventFormColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF5D2AD7), // purple when focused
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White.copy(alpha = 0.6f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedPlaceholderColor = Color.White,   // placeholder becomes white when focused
    unfocusedPlaceholderColor = Color.Black, // placeholder black when unfocused
    focusedTextColor = Color.White,
    unfocusedTextColor = Color(0xFF090040),
    cursorColor = Color.White
)

@Composable
private fun eventOutlinedColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF5D2AD7),
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White.copy(alpha = 0.6f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.Black,
    focusedPlaceholderColor = Color.White,
    unfocusedPlaceholderColor = Color.Black,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color(0xFF090040),
    cursorColor = Color.White
)