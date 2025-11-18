package com.example.projetorole.ui.cupons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.projetorole.data.model.Cupom
import com.example.projetorole.R

@Composable
fun CuponsScreen(
    isEstabelecimento: Boolean = false,
    onCreateCupom: () -> Unit = {},
    cuponsViewModel: CuponsViewModel = viewModel()
) {
    val cupons by cuponsViewModel.cupons.collectAsState()

    LaunchedEffect(Unit) {
        cuponsViewModel.carregarCupons()
    }

    Scaffold(
        floatingActionButton = {
            if (isEstabelecimento) {
                FloatingActionButton(
                    onClick = onCreateCupom,
                    containerColor = Color(0xFFFFCC00),
                    contentColor = Color(0xFF090040)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Cupom")
                }
            }
        },
        containerColor = Color(0xFF090040)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = if (isEstabelecimento) "Gerenciar Cupons" else "Cupons Disponíveis",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (cupons.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Nenhum cupom encontrado",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (isEstabelecimento) "Crie seu primeiro cupom agora!" else "Fique de olho para novidades!",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cupons) { cupom ->
                        CupomCard(
                            cupom = cupom,
                            showDelete = isEstabelecimento,
                            onDelete = { cuponsViewModel.deletarCupom(cupom.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CupomCard(
    cupom: Cupom,
    showDelete: Boolean,
    onDelete: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.cupom_icon),
            contentDescription = "Cupom",
            modifier = Modifier.size(60.dp)
        )

        Spacer(Modifier.width(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFECE6F0)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        cupom.titulo,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    if (cupom.disponivel) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Disponível",
                                color = Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            cupom.local,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                if (showDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir Cupom",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}