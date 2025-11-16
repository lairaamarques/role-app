package com.example.projetorole.ui.detail

import android.Manifest // Para as permissões
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


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
import androidx.compose.ui.platform.LocalContext // Import para o Context
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun DetalheEventoScreen(
    eventoId: Int,
    viewModel: DetalheEventoViewModel = viewModel(),
    onBack: () -> Unit,
    onCheckinSuccess: () -> Unit
) {

    val checkInState by viewModel.checkInState.collectAsState()
    val evento by viewModel.evento.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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

    when (val state = checkInState) {
        is CheckInUiState.Loading -> {

        }
        is CheckInUiState.Success -> {
            Toast.makeText(context, "Check-in realizado com sucesso!", Toast.LENGTH_LONG).show()
            LaunchedEffect(Unit) {
                viewModel.resetCheckInState()
                onCheckinSuccess()
            }
        }
        is CheckInUiState.Error -> {
            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            LaunchedEffect(Unit) {
                viewModel.resetCheckInState()
            }
        }
        is CheckInUiState.Idle -> { }
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
                    .height(200.dp)
                    .background(Color(0xFFE8E8E8))
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }


                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFB8B8B8), CircleShape)
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFB8B8B8), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(40.dp)
                                .background(Color(0xFFB8B8B8), RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

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
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Termômetro Social",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = {

                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (checkInState is CheckInUiState.Success) Color(0xFF4CAF50) else Color(0xFF00C853),
                                disabledContainerColor = Color(0xFF6750A4)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(36.dp),

                            enabled = checkInState is CheckInUiState.Idle
                        ) {

                            when (checkInState) {
                                is CheckInUiState.Idle -> Text("● Check-in", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                is CheckInUiState.Loading -> Text("Verificando...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                is CheckInUiState.Success -> Text("✓ Feito!", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                is CheckInUiState.Error -> Text("● Check-in!", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                Button(
                    onClick = {  },
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
                        is CheckInUiState.Error -> Text("Tentar Novamente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
