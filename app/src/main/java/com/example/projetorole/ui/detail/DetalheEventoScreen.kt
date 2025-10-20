package com.example.projetorole.ui.detail

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.data.model.Evento

@Composable
fun DetalheEventoScreen(
    evento: Evento,
    viewModel: DetalheEventoViewModel = viewModel(),
    onBack: () -> Unit,
    onCheckin: (Evento) -> Unit
) {
    val isCheckedIn by viewModel.isCheckedIn.collectAsState()
    
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
                evento.nome,
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
                    evento.local,
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
                    evento.getDataFormatada(),
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
                        onClick = { viewModel.fazerCheckin() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCheckedIn) Color(0xFF4CAF50) else Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            if (isCheckedIn) "✓ Feito!" else "● Bombando!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${evento.checkIns}",
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
                evento.nome,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                evento.descricao ?: "Sem descrição",
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
                    evento.getPrecoFormatado(),
                    color = if (evento.pago && evento.preco != null) Color(0xFFFFCC00) else Color(0xFF4CAF50),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Sem lógica por enquanto */ },
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
                    if (!isCheckedIn) {
                        viewModel.fazerCheckin()
                        onCheckin(evento)
                    }
                },
                modifier = Modifier
                    .width(240.dp)
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = !isCheckedIn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCC00),
                    contentColor = Color(0xFF090040),
                    disabledContainerColor = Color(0xFF6750A4),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isCheckedIn) "Check-in realizado" else "Realizar Check-in",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}