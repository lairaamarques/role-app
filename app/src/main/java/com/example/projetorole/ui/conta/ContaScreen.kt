package com.example.projetorole.ui.conta

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.R

@Composable
fun ContaScreen(
    isEstabelecimento: Boolean,
    onManageEvents: () -> Unit,
    contaViewModel: ContaViewModel = ContaViewModel()
) {
    val usuario by contaViewModel.usuario.collectAsState()
    val diasAgenda by contaViewModel.diasAgenda.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        PerfilSection(usuario = usuario)

        Spacer(Modifier.height(48.dp))


        EstatisticasSection(usuario = usuario)

        Spacer(Modifier.height(48.dp))


        AgendaSection(dias = diasAgenda)

        Spacer(Modifier.height(48.dp))

        MenuSection(
            showManageEvents = isEstabelecimento,
            onManageEvents = onManageEvents,
            onLogout = contaViewModel::logout
        )
    }
}

@Composable
private fun PerfilSection(usuario: Usuario) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar_placeholder),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            usuario.nome,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            usuario.email,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EstatisticasSection(usuario: Usuario) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        EstatisticaItem(
            numero = usuario.checkinsRealizados.toString(),
            label = "check-ins\nrealizados"
        )
        EstatisticaItem(
            numero = usuario.checkinsSalvos.toString(),
            label = "check-ins\nsalvos"
        )
    }
}

@Composable
private fun EstatisticaItem(numero: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            numero,
            color = Color(0xFFFFCC00),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AgendaSection(dias: List<DiaAgenda>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Agenda",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dias) { dia ->
                DiaAgendaCard(dia = dia)
            }
        }
    }
}

@Composable
private fun DiaAgendaCard(dia: DiaAgenda) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCC00))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                dia.dia.toString(),
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                dia.diaSemana,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MenuSection(
    showManageEvents: Boolean,
    onManageEvents: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MenuItem(
            icon = Icons.Default.Favorite,
            text = "Locais Favoritos"
        )
        MenuItem(
            icon = Icons.Default.Info,
            text = "Minhas informações"
        )
        if (showManageEvents) {
            Button(
                onClick = onManageEvents,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00)),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Gerenciar eventos", color = Color(0xFF090040))
            }
        }
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF471396)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = "Sair",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Sair", color = Color.White)
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(20.dp))
        Text(
            text,
            color = Color.White,
            fontSize = 20.sp
        )
    }
}