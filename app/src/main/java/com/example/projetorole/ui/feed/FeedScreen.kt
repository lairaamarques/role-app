package com.example.projetorole.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.data.model.Evento
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel // ← ATUALIZADO

// Mock de categorias expandido para demonstrar scroll
private val categorias = listOf(
    "Ocorrendo hoje",
    "Bares",
    "Show",
    "Festa",
    "Restaurantes",
    "Baladas",
    "Karaokê",
    "Music Bar",
    "Teatro",
    "Cinema"
)

@Composable
fun FeedScreen(
    onEventoClick: (Evento) -> Unit = {},
    checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel() // ← ATUALIZADO
) {
    // Usar ViewModel para dados consistentes
    val viewModel: FeedViewModel = viewModel()
    val eventos by viewModel.eventos.collectAsState()

    var categoriaSelecionada by remember { mutableIntStateOf(0) }
    var textoBusca by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
    ) {
        // Localização
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
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Selecionar localização",
                    tint = Color(0xFFFEF7FF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Barra de busca - corrigida para mostrar placeholder e texto
        OutlinedTextField(
            value = textoBusca,
            onValueChange = { textoBusca = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .defaultMinSize(minHeight = 37.dp), // ← Altura mínima em vez de fixa
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
                focusedBorderColor = Color.Transparent, // ← Remove borda para parecer mais baixo
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

        // Espaçamento aumentado entre busca e categorias (igual ao do feed)
        Spacer(Modifier.height(16.dp))

        // Categorias com scroll horizontal
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(categorias) { idx, categoria ->
                val selected = idx == categoriaSelecionada
                AssistChip(
                    onClick = { categoriaSelecionada = idx },
                    label = {
                        Text(
                            categoria,
                            color = if (selected) Color(0xFF6750A4) else Color(0xFFFEF7FF),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (selected) Color(0xFF6750A4) else Color(0xFFFEF7FF),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Color(0xFFFEF7FF) else Color(0xFF6750A4),
                        labelColor = if (selected) Color(0xFF6750A4) else Color(0xFFFEF7FF)
                    ),
                    border = null,
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        // Espaçamento igual ao que estava antes (16.dp)
        Spacer(Modifier.height(16.dp))

        // Feed de eventos
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filtrar eventos baseado na busca
            val eventosFiltrados = if (textoBusca.isBlank()) {
                eventos
            } else {
                eventos.filter { evento ->
                    evento.nome.contains(textoBusca, ignoreCase = true) ||
                            evento.local.contains(textoBusca, ignoreCase = true)
                }
            }

            items(eventosFiltrados) { evento ->
                EventoCard(
                    evento = evento, 
                    onClick = { onEventoClick(evento) },
                    checkinsSalvosViewModel = checkinsSalvosViewModel // ← ATUALIZADO
                )
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
            // Placeholder da imagem
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFFE8E8E8),
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFB8B8B8), CircleShape)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFFB8B8B8), CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(14.dp)
                                .background(Color(0xFFB8B8B8), RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            // Conteúdo do evento
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // SEÇÃO SUPERIOR - Título mais próximo da borda
                Column {
                    // Título com ícone de favorito
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
                        
                        // Ícone de favorito
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

                    // Local - LINHA SEPARADA (SEM ESPAÇAMENTO)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn, // ← MUDADO DE Place PARA LocationOn
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

                    // Data e horário - LINHA SEPARADA ABAIXO DO LOCAL (SEM ESPAÇAMENTO)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFEF7FF),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "Hoje, ${evento.horario}",
                            color = Color(0xFFFEF7FF),
                            fontSize = 9.sp
                        )
                    }
                }

                // SEÇÃO INFERIOR - Preço e Check-ins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Preço - BOX COM LARGURA FIXA PARA AMBOS OS CASOS
                    Box(
                        modifier = Modifier
                            .width(70.dp) // ← LARGURA FIXA PARA UNIFORMIZAR
                            .background(
                                if (evento.pago) Color(0xFFFFCC00) else Color(0xFF4CAF50),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 4.dp), // ← APENAS PADDING VERTICAL
                        contentAlignment = Alignment.Center // ← CENTRALIZADO
                    ) {
                        Text(
                            text = evento.getPrecoFormatado(),
                            color = Color(0xFF000000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Check-ins
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