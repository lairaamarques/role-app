package com.example.projetorole.ui.cupons

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetorole.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CupomUsedScreen(
    cupomId: Int,
    timestamp: Long,
    cupomTitle: String? = null,
    cupomDesc: String? = null,
    cuponsViewModel: CuponsViewModel = viewModel(),
    onBack: () -> Unit
) {
    val cupons by cuponsViewModel.cupons.collectAsState()
    val title = cupomTitle.takeIf { !it.isNullOrBlank() } ?: cupons.find { it.id == cupomId }?.titulo
    val desc = cupomDesc.takeIf { !it.isNullOrBlank() } ?: cupons.find { it.id == cupomId }?.descricao

    val formatted = runCatching {
        val instant = Instant.ofEpochMilli(timestamp)
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }.getOrDefault("")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cupom usado", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090040))
            )
        },
        containerColor = Color(0xFF090040)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Image(
                    painter = painterResource(id = R.drawable.checked_icon),
                    contentDescription = "Cupom usado",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(100.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Cupom utilizado!",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF5D2AD7))
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title ?: "Cupom",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!desc.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Usado em: $formatted",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Obrigado! Aproveite o Rolê.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}