package com.example.projetorole.ui.conta

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.widget.Toast
import com.example.projetorole.R
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.repository.EstabelecimentoEventosRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEstabelecimentoScreen(
    onBack: () -> Unit,
    onManageEvents: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    val profile by AuthRepository.profile.collectAsState()

    val eventosRepo = remember { EstabelecimentoEventosRepository() }

    class Factory(
        private val eventosRepo: EstabelecimentoEventosRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileEstabelecimentoViewModel(
                fetchEventos = { eventosRepo.getMeusEventos() },
                fetchCupons = { emptyList() },
                fetchCheckins = {
                    val ev = eventosRepo.getMeusEventos()
                    val total = ev.sumOf { it.checkIns }
                    List(total) { Any() }
                }
            ) as T
        }
    }

    val vm: ProfileEstabelecimentoViewModel = viewModel(factory = Factory(eventosRepo))
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val totalEvents by vm.totalEventos.collectAsState()
    val totalCheckins by vm.totalCheckins.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            if (!profile?.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = profile!!.photoUrl,
                    contentDescription = "Foto estabelecimento",
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
                text = profile?.displayName?.takeIf { it.isNotBlank() } ?: "Estabelecimento",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = profile?.email ?: "",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            error?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$totalEvents",
                        color = Color(0xFFFFCC00),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text("eventos", color = Color.LightGray, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$totalCheckins",
                        color = Color(0xFFFFCC00),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text("check-ins", color = Color.LightGray, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onManageEvents,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCC00),
                    contentColor = Color(0xFF090040)
                )
            ) {
                Text("Gerenciar meus eventos", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    kotlin.runCatching { onEditProfile() }.onFailure {
                        Toast.makeText(context, "Não foi possível abrir edição de perfil", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Text("Minhas informações", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

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
}