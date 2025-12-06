package com.example.projetorole.ui.feed

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projetorole.data.model.Evento
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import com.example.projetorole.R

@Composable
fun FeedScreen(
    onEventoClick: (Evento) -> Unit = {},
    checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel()
) {
    val viewModel: FeedViewModel = viewModel()
    val eventos by viewModel.eventos.collectAsState()

    var textoBusca by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Localização",
                color = Color(0xFFFEF7FF),
                fontSize = 14.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manaus, Amazonas",
                    color = Color(0xFFFEF7FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        OutlinedTextField(
            value = textoBusca,
            onValueChange = { textoBusca = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .defaultMinSize(minHeight = 37.dp),
            placeholder = {
                Text(
                    "Buscar rolê",
                    color = Color(0xFFFEF7FF).copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFFFEF7FF),
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color(0xFFFEF7FF),
                unfocusedTextColor = Color(0xFFFEF7FF),
                cursorColor = Color(0xFFFEF7FF),
                focusedContainerColor = Color(0xFF6750A4),
                unfocusedContainerColor = Color(0xFF6750A4)
            ),
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val eventosFiltrados = if (textoBusca.isBlank()) {
                eventos
            } else {
                eventos.filter { evento ->
                    evento.nome.contains(textoBusca, ignoreCase = true) ||
                            evento.local.contains(textoBusca, ignoreCase = true)
                }
            }

            if (eventosFiltrados.isEmpty()) {
                items(5) {
                    ShimmerEventoCard()
                }
            } else {
                items(eventosFiltrados) { evento ->
                    EventoCard(
                        evento = evento,
                        onClick = { onEventoClick(evento) },
                        checkinsSalvosViewModel = checkinsSalvosViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun EventoCard(
    evento: Evento,
    onClick: () -> Unit,
    checkinsSalvosViewModel: CheckinsSalvosViewModel
) {
    val checkinsSalvos by checkinsSalvosViewModel.checkinsSalvos.collectAsState()
    val isFavorito = checkinsSalvos.any { it.eventoId == evento.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF471396))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            ) {
                if (!evento.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = evento.imageUrl,
                        contentDescription = evento.nome,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.event_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            evento.nome,
                            color = Color(0xFFFEF7FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )

                        IconButton(
                            onClick = { checkinsSalvosViewModel.toggleSalvarCheckin(evento.id) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorito) "Remover dos salvos" else "Salvar evento",
                                tint = if (isFavorito) Color(0xFFFFCC00) else Color(0xFFFEF7FF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFEF7FF),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            evento.local,
                            color = Color(0xFFFEF7FF),
                            fontSize = 9.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFEF7FF),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            evento.getDataFormatada(),
                            color = Color(0xFFFEF7FF),
                            fontSize = 9.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .background(
                                if (evento.pago) Color(0xFFFFCC00) else Color(0xFF4CAF50),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = evento.getPrecoFormatado(),
                            color = Color(0xFF000000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${evento.checkIns}",
                            color = Color(0xFFFFCC00),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "Check-ins",
                            color = Color(0xFFFEF7FF),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerEventoCard() {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1000))
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF5B3F9A).copy(alpha = 0.55f),
            Color(0xFF7B63C9).copy(alpha = 0.55f),
            Color(0xFF5B3F9A).copy(alpha = 0.55f)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 200f, 200f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(brush)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(brush, RoundedCornerShape(6.dp))
                )
                Box(modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .background(brush, RoundedCornerShape(6.dp))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .width(70.dp)
                        .height(28.dp)
                        .background(brush, RoundedCornerShape(6.dp))
                    )
                    Box(modifier = Modifier
                        .width(80.dp)
                        .height(28.dp)
                        .background(brush, RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}