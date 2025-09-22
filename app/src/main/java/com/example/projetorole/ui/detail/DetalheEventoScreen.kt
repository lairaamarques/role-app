package com.example.projetorole.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onBack: () -> Unit
) {
    val isCheckedIn by viewModel.isCheckedIn.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3C6FF))
    ) {
        // Placeholder da imagem do evento com seta de voltar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE8E8E8))
        ) {
            // Seta de voltar no canto superior esquerdo
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
            
            // Placeholder centralizado
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ícone triangular grande
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFB8B8B8), CircleShape)
                )
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Ícone estrela/engrenagem
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFB8B8B8), CircleShape)
                    )
                    
                    // Ícone retangular
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(40.dp)
                            .background(Color(0xFFB8B8B8), RoundedCornerShape(8.dp))
                    )
                }
            }
        }
        
        // Seção de informações - fundo roxo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(20.dp)
        ) {
            // Nome do evento
            Text(
                evento.nome,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Data
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
                    "Hoje",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            
            // Local
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
            
            // Horário
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Horário",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            
            // Termômetro Social com Check-ins
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF471396), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                // Primeira linha: Ícone + Título + Botão
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
                
                // Segunda linha: Check-ins count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${evento.checkIns}",
                        color = Color(0xFFFFCC00),
                        fontSize = 48.sp,
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
            
            // Título do evento (removido o número de check-ins daqui)
            Text(
                evento.nome,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Descrição
            Text(
                "Prepare o chapéu e a bota: venha para a Noite Sertaneja do Rancho Estrela!\n\nSe você ama um bom modão, dança agarradinha e aquele clima de festa do interior, não perca!",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}