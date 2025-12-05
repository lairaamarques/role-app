package com.example.projetorole.ui.conta

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.projetorole.R
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ContaScreen(
    isEstabelecimento: Boolean,
    onManageEvents: () -> Unit,
    onCuponsClick: () -> Unit,
    contaViewModel: ContaViewModel,
    onSavedDayClick: (dateIso: String) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    feedViewModel: FeedViewModel = viewModel(),
    checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel()
) {
    val usuario by contaViewModel.usuario.collectAsState()
    val profile by AuthRepository.profile.collectAsState()
    val eventos by feedViewModel.eventos.collectAsState()
    val checkinsSalvos by checkinsSalvosViewModel.checkinsSalvos.collectAsState()

    val savedEventsByDate = remember(eventos, checkinsSalvos) {
        val savedIds = checkinsSalvos.map { it.eventoId }.toSet()
        eventos.filter { it.id in savedIds }
            .groupBy { it.horario.take(10) }
    }

    val sortedDates = remember(savedEventsByDate) {
        savedEventsByDate.keys.sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(Modifier.height(16.dp))

        PerfilSection(usuario = usuario, profile = profile)

        Spacer(Modifier.height(48.dp))

        EstatisticasSection(usuario = usuario)

        Spacer(Modifier.height(48.dp))

        if (savedEventsByDate.isNotEmpty()) {
            Text(
                text = "Agenda",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedDates) { dateIso ->
                    val count = savedEventsByDate[dateIso]?.size ?: 0
                    val localDate = runCatching { LocalDate.parse(dateIso) }.getOrNull()
                    val dayName = localDate?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale("pt", "BR")) ?: ""
                    val dayNum = localDate?.dayOfMonth?.toString() ?: dateIso

                    Card(
                        modifier = Modifier
                            .width(70.dp)
                            .clickable { onSavedDayClick(dateIso) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF471396))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(dayName, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(dayNum, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            if (count > 0) {
                                Text("$count", color = Color(0xFFFFCC00), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(48.dp))

        MenuSection(
            showManageEvents = isEstabelecimento,
            onManageEvents = onManageEvents,
            onLogout = onLogout,
            onCuponsClick = onCuponsClick,
            onEditProfile = onEditProfile
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PerfilSection(usuario: Usuario, profile: com.example.projetorole.data.auth.AuthRepository.AuthProfile?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!profile?.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = profile!!.photoUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.tower_avatar_placeholder),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = profile?.displayName?.takeIf { it.isNotBlank() } ?: usuario.nome,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = profile?.email ?: usuario.email,
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
            text = numero,
            color = Color(0xFFFFCC00),
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MenuSection(
    showManageEvents: Boolean,
    onManageEvents: () -> Unit,
    onLogout: () -> Unit,
    onCuponsClick: () -> Unit,
    onEditProfile: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onCuponsClick,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF471396))
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Meus cupons",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = {
                kotlin.runCatching { onEditProfile() }.onFailure {
                    Toast.makeText(context, "Não foi possível abrir edição de perfil", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f))
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Minhas informações",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (showManageEvents) {
            Button(
                onClick = onManageEvents,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = Color(0xFF090040),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Gerenciar eventos",
                    color = Color(0xFF090040),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF471396)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Sair",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Sair", color = Color.White)
        }
    }
}