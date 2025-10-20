package com.example.projetorole.ui.conta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.projetorole.data.auth.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEstabelecimentoScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by AuthRepository.profile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Perfil") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                profile?.displayName ?: "Estabelecimento",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                profile?.email.orEmpty(),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "CNPJ: Não informado",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF471396)),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Sair", color = Color.White)
            }
        }
    }
}