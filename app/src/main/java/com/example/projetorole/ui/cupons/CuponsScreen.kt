package com.example.projetorole.ui.cupons

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuponsScreen(
    cuponsViewModel: CuponsViewModel = viewModel(),
    navController: NavHostController
) {
    val cupons by cuponsViewModel.cupons.collectAsState()
    val isLoading by cuponsViewModel.isLoading.collectAsState()
    val error by cuponsViewModel.error.collectAsState()

    var showConfirm by remember { mutableStateOf(false) }
    var selectedCupom by remember { mutableStateOf<Cupom?>(null) }

    LaunchedEffect(Unit) {
        cuponsViewModel.carregarCupons()
    }

    if (showConfirm && selectedCupom != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false; selectedCupom = null },
            title = { Text("Confirmar uso do cupom") },
            text = { Text("Tem certeza que deseja usar o cupom \"${selectedCupom?.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    val cupom = selectedCupom!!
                    showConfirm = false
                    selectedCupom = null
                    cuponsViewModel.usarCupom(cupom.id)
                    val ts = System.currentTimeMillis()
                    val encodedTitle = URLEncoder.encode(cupom.titulo, StandardCharsets.UTF_8.toString())
                    val encodedDesc = URLEncoder.encode(cupom.descricao ?: "", StandardCharsets.UTF_8.toString())
                    navController.navigate("cupom_used/${cupom.id}/${ts}/${encodedTitle}/${encodedDesc}")
                }) {
                    Text("Usar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false; selectedCupom = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF090040), Color(0xFF1A0A5C))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Meus Cupons",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                cupons.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Você ainda não tem cupons",
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cupons, key = { it.id }) { cupom ->
                            CupomCard(
                                cupom = cupom,
                                onUseCupom = {
                                    selectedCupom = cupom
                                    showConfirm = true
                                },
                                onDeleteCupom = {
                                    cuponsViewModel.deletarCupom(cupom.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CupomCard(
    cupom: Cupom,
    onUseCupom: () -> Unit,
    onDeleteCupom: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .padding(end = 12.dp)
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cupom_icon),
                contentDescription = "Ícone cupom",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .animateContentSize()
                .clickable(enabled = !cupom.usado) { if (!cupom.usado) onUseCupom() }
                .clip(RoundedCornerShape(12.dp)),
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
                        text = cupom.titulo,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = cupom.descricao ?: "",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = cupom.estabelecimentoNome ?: "",
                        color = Color(0xFF6C63FF),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(72.dp)
                ) {
                    if (!cupom.usado) {
                        Button(
                            onClick = onUseCupom,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Usar", color = Color.White)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Usado", color = Color.Gray, fontSize = 13.sp)
                            IconButton(onClick = onDeleteCupom) {
                                Icon(Icons.Default.Delete, contentDescription = "Apagar cupom", tint = Color(0xFFF44336))
                            }
                        }
                    }

                    if (!cupom.usado) {
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(onClick = onDeleteCupom) {
                            Icon(Icons.Default.Delete, contentDescription = "Apagar cupom", tint = Color(0xFFF44336))
                        }
                    }
                }
            }
        }
    }
}