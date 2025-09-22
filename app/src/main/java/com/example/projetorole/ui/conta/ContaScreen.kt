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
    contaViewModel: ContaViewModel = viewModel()
) {
    val usuario by contaViewModel.usuario.collectAsState()
    val diasAgenda by contaViewModel.diasAgenda.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // ← CENTRALIZADO
        verticalArrangement = Arrangement.Center // ← CENTRALIZADO VERTICALMENTE
    ) {
        // Seção do perfil - MAIOR E CENTRALIZADA
        PerfilSection(usuario = usuario)
        
        Spacer(Modifier.height(48.dp)) // ← AUMENTADO
        
        // Seção de estatísticas - MAIOR
        EstatisticasSection(usuario = usuario)
        
        Spacer(Modifier.height(48.dp)) // ← AUMENTADO
        
        // Agenda - MAIOR
        AgendaSection(dias = diasAgenda)
        
        Spacer(Modifier.height(48.dp)) // ← AUMENTADO
        
        // Menu de opções - MAIOR
        MenuSection()
    }
}

@Composable
private fun PerfilSection(usuario: Usuario) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally // ← CENTRALIZADO
    ) {
        // Avatar personalizado - MAIOR
        Image(
            painter = painterResource(id = R.drawable.avatar_placeholder), // ← IMAGEM PERSONALIZADA
            contentDescription = "Avatar",
            modifier = Modifier
                .size(120.dp) // ← AUMENTADO DE 80dp PARA 120dp
                .clip(CircleShape)
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Nome e idade centralizados
        Text(
            usuario.nome,
            color = Color.White,
            fontSize = 28.sp, // ← AUMENTADO DE 20sp PARA 28sp
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "${usuario.idade} anos",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 18.sp, // ← AUMENTADO DE 14sp PARA 18sp
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
            fontSize = 48.sp, // ← AUMENTADO DE 32sp PARA 48sp
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = Color.White,
            fontSize = 16.sp, // ← AUMENTADO DE 12sp PARA 16sp
            lineHeight = 18.sp, // ← AUMENTADO
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AgendaSection(dias: List<DiaAgenda>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally // ← CENTRALIZADO
    ) {
        Text(
            "Agenda",
            color = Color.White,
            fontSize = 24.sp, // ← AUMENTADO DE 18sp PARA 24sp
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp) // ← AUMENTADO
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp) // ← AUMENTADO DE 8dp PARA 12dp
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
            .width(60.dp) // ← AUMENTADO DE 50dp PARA 60dp
            .height(80.dp), // ← AUMENTADO DE 70dp PARA 80dp
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCC00))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp), // ← AUMENTADO DE 4dp PARA 6dp
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                dia.dia.toString(),
                color = Color.Black,
                fontSize = 22.sp, // ← AUMENTADO DE 18sp PARA 22sp
                fontWeight = FontWeight.Bold
            )
            Text(
                dia.diaSemana,
                color = Color.Black,
                fontSize = 12.sp, // ← AUMENTADO DE 10sp PARA 12sp
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MenuSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp), // ← AUMENTADO DE 16dp PARA 24dp
        horizontalAlignment = Alignment.CenterHorizontally // ← CENTRALIZADO
    ) {
        MenuItem(
            icon = Icons.Default.Favorite,
            text = "Locais Favoritos"
        )
        MenuItem(
            icon = Icons.Default.Info,
            text = "Minhas informações"
        )
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
            modifier = Modifier.size(32.dp) // ← AUMENTADO DE 24dp PARA 32dp
        )
        Spacer(Modifier.width(20.dp)) // ← AUMENTADO DE 16dp PARA 20dp
        Text(
            text,
            color = Color.White,
            fontSize = 20.sp // ← AUMENTADO DE 16sp PARA 20sp
        )
    }
}