package com.example.projetorole.ui.conta

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import com.example.projetorole.data.model.Evento
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEventsByDayScreen(
    dateIso: String,
    onBack: () -> Unit,
    onEventoClick: (Int) -> Unit,
    feedViewModel: FeedViewModel = viewModel(),
    checkinsViewModel: CheckinsSalvosViewModel = viewModel()
) {
    val eventos by feedViewModel.eventos.collectAsState()
    val checkinsSalvos by checkinsViewModel.checkinsSalvos.collectAsState()

    val savedIds = checkinsSalvos.map { it.eventoId }.toSet()
    val eventosDoDia: List<Evento> = eventos.filter { it.id in savedIds && it.horario.startsWith(dateIso) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val pretty = runCatching {
                        LocalDate.parse(dateIso).format(DateTimeFormatter.ofPattern("dd/MM"))
                    }.getOrDefault(dateIso)
                    Text(text = "Eventos salvos: $pretty", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090040))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(16.dp)
        ) {
            if (eventosDoDia.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum evento salvo neste dia.", color = Color.White.copy(alpha = 0.7f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(eventosDoDia, key = { _, e -> e.id }) { index, evento ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clickable { onEventoClick(evento.id) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF471396))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(evento.nome, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(6.dp))
                                Text(evento.local, color = Color.White.copy(alpha = 0.8f))
                                Spacer(Modifier.height(6.dp))
                                Text("${evento.getDataFormatada()} • ${evento.getHoraFormatada()}", color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}