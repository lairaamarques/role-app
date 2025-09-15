
package com.example.projetorole.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projetorole.data.model.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
	eventos: List<Evento>,
	onEventoClick: (Evento) -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
						Text(
							"ROLÊ",
							style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold)
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color(0xFFE3F0FF), // azul claro igual detalhes
					titleContentColor = Color.Black
				)
			)
		},
		containerColor = Color.Transparent
	) { padding ->
		LazyColumn(
			contentPadding = padding,
			modifier = Modifier.fillMaxSize()
		) {
			items(eventos) { evento ->
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp)
						.clickable { onEventoClick(evento) },
					shape = RoundedCornerShape(20.dp),
					colors = CardDefaults.cardColors(
						containerColor = Color.White
					),
					elevation = CardDefaults.cardElevation(8.dp)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						// Imagem placeholder
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.height(120.dp)
								.background(Color(0xFFE3F0FF), RoundedCornerShape(16.dp)), // azul claro
							contentAlignment = Alignment.Center
						) {
							Icon(
								imageVector = Icons.Default.DateRange,
								contentDescription = "Imagem do evento",
								tint = Color(0xFF6C3DD1), // roxo
								modifier = Modifier.size(48.dp)
							)
						}
						Spacer(Modifier.height(12.dp))
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text(evento.nome, style = MaterialTheme.typography.titleMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold))
							Spacer(Modifier.width(8.dp))
							// ...removido chip de evento pago...
						}
						Spacer(Modifier.height(4.dp))
						Text("Horário: ${evento.horario}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
						Text("Local: ${evento.local}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
						Spacer(Modifier.height(4.dp))
						Row {
							Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6C3DD1))
							Spacer(Modifier.width(4.dp))
							Text("Check-ins: ${evento.checkIns}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
						}
						evento.nota?.let {
							Spacer(Modifier.height(4.dp))
							Row {
								Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFE3F0))
								Spacer(Modifier.width(4.dp))
								Text("Nota: $it", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
							}
						}
					}
				}
				HorizontalDivider (modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
			}
		}
	}
}