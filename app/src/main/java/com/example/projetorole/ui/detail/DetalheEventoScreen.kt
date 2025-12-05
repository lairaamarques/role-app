package com.example.projetorole.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.R
import androidx.compose.ui.platform.LocalDensity

@Composable
fun DetalheEventoScreen(
    eventoId: Int,
    viewModel: DetalheEventoViewModel = viewModel(),
    onBack: () -> Unit,
    onCheckinSuccess: (Cupom?) -> Unit
) {

    val checkInState by viewModel.checkInState.collectAsState()
    val evento by viewModel.evento.collectAsState()
    val eventoState = evento
    val isLoading by viewModel.isLoading.collectAsState()

    if (checkInState is CheckInUiState.Far && eventoState != null) {
        val ts = (checkInState as CheckInUiState.Far).timestamp
        CheckinFarScreen(evento = eventoState, timestampMillis = ts) {
            viewModel.resetCheckInState()
        }
        return
    }

    val context = LocalContext.current

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {

                getCurrentLocation(context, locationClient) { latitude, longitude ->
                    viewModel.performCheckIn(latitude, longitude)
                }
            } else {
                Toast.makeText(context, "Permissão de localização é necessária.", Toast.LENGTH_LONG).show()
            }
        }
    )


    LaunchedEffect(eventoId) {
        viewModel.loadEvento(eventoId)
    }

    LaunchedEffect(checkInState) {
        when (val state = checkInState) {
            is CheckInUiState.Success -> {
                val mensagem = state.cupom?.let { "Check-in garantido! Cupom desbloqueado: ${it.titulo}" }
                    ?: "Check-in realizado com sucesso!"
                Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                viewModel.resetCheckInState()
                onCheckinSuccess(state.cupom)
            }
            is CheckInUiState.Far -> {
            }
            is CheckInUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetCheckInState()
            }
            else -> Unit
        }
    }


    if (isLoading) {

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF090040)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (evento != null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape),
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (!eventoState?.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = eventoState!!.imageUrl,
                        contentDescription = eventoState.nome,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.event_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF090040))
                    .padding(20.dp)
            ) {
                Text(
                    evento!!.nome,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        evento!!.local,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        evento!!.getDataFormatada(),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF471396), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.hot_icon),
                                contentDescription = "Bombando",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Termômetro Social",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        if ((evento?.checkIns ?: 0) > 3) {
                            val textSize = 12.sp
                            val iconSizeDp = with(LocalDensity.current) { textSize.toDp() }
                            val transition = rememberInfiniteTransition()
                            val scaleAnim by transition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.06f,
                                animationSpec = infiniteRepeatable(tween(durationMillis = 700))
                            )
                            Surface(
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(start = 8.dp)
                                    .scale(scaleAnim),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFE04A19)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .height(36.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.hot_icon),
                                        contentDescription = "Bombando",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(iconSizeDp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Bombando!",
                                        color = Color.White,
                                        fontSize = textSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${evento!!.checkIns}",
                            color = Color(0xFFFFCC00),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Check-ins",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    evento!!.nome,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    evento!!.descricao ?: "Sem descrição",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF471396), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Valor do ingresso:",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        evento!!.getPrecoFormatado(),
                        color = if (evento!!.pago && evento!!.preco != null) Color(0xFFFFCC00) else Color(0xFF4CAF50),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                val paymentLink = evento!!.paymentLink
                val uriHandler = LocalUriHandler.current
                if (!paymentLink.isNullOrBlank()) {
                    Button(
                        onClick = { uriHandler.openUri(paymentLink) },
                        modifier = Modifier
                            .width(240.dp)
                            .height(60.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFCC00),
                            contentColor = Color(0xFF090040)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Comprar Ingresso", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = (checkInState is CheckInUiState.Idle || checkInState is CheckInUiState.Error),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCC00),
                        contentColor = Color(0xFF090040),
                        disabledContainerColor = Color(0xFF6750A4),
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    when (checkInState) {
                        is CheckInUiState.Idle -> Text("Realizar Check-in", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        is CheckInUiState.Loading -> Text("Verificando...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        is CheckInUiState.Success -> Text("Check-in Feito ✓", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        is CheckInUiState.Far -> Text("Longe do evento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        is CheckInUiState.Error -> Text("Tentar Novamente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                if (!evento!!.cupomTitulo.isNullOrBlank()) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Recompensa do rolê",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCC00).copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = evento!!.cupomTitulo!!,
                                color = Color(0xFFFFCC00),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            evento!!.cupomDescricao?.let {
                                Text(
                                    text = it,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Ganhe após ${evento!!.cupomCheckinsNecessarios} check-in(s).",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    } else {

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF090040)), contentAlignment = Alignment.Center) {
            Text("Evento não encontrado.", color = Color.White)
        }
    }
}


@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    locationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationFetched: (latitude: Double, longitude: Double) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }

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
