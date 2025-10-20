package com.example.projetorole.ui.salvos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.data.model.Evento
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.R

@Composable
fun CheckinsSalvosScreen(
    onEventoClick: (Evento) -> Unit = {},
    checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel(),
    feedViewModel: FeedViewModel = viewModel()
) {
    val checkinsSalvos by checkinsSalvosViewModel.checkinsSalvos.collectAsState()
    val eventos by feedViewModel.eventos.collectAsState()
    
    val checkinsSalvosFiltrados = eventos.filter { evento ->
        checkinsSalvos.any { it.eventoId == evento.id }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
            .padding(24.dp)
    ) {
        Text(
            text = "Check-ins salvos",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Salve seus rolês para não se perder!",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (checkinsSalvosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Nenhum check-in salvo",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Salve eventos no feed para fazer check-ins depois!",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(checkinsSalvosFiltrados) { evento ->
                    CheckinSalvoCard(
                        evento = evento,
                        onClick = { onEventoClick(evento) },
                        onRemove = { checkinsSalvosViewModel.removerCheckinSalvo(evento.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckinSalvoCard(
    evento: Evento,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.favorite_icon),
            contentDescription = "Favorito",
            modifier = Modifier.size(60.dp)
        )
        
        Spacer(Modifier.width(12.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECE6F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        evento.nome,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            evento.local,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${evento.getDataFormatada()} às ${evento.getHoraFormatada()}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir check-in salvo",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}