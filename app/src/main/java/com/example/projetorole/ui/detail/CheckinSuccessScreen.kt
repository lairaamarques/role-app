package com.example.projetorole.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetorole.R
import com.example.projetorole.data.model.Evento
import kotlin.math.roundToInt

@Composable
fun CheckinSuccessScreen(
    evento: Evento,
    cupomTitulo: String? = null,
    cupomDescricao: String? = null,
    cupomEstabelecimento: String? = null,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val shakeOffset = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                repeat(6) { i ->
                    val target = if (i % 2 == 0) -18f else 18f
                    shakeOffset.animateTo(target, animationSpec = tween(durationMillis = 55))
                }
                shakeOffset.animateTo(0f, animationSpec = tween(durationMillis = 120))
            }

            Image(
                painter = painterResource(id = R.drawable.checked_icon),
                contentDescription = "Check-in realizado",
                modifier = Modifier
                    .size(200.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Check-in realizado!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Sua presença em ${evento.nome} foi confirmada",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .background(Color(0xFF5D2AD7), RoundedCornerShape(16.dp))
                    .padding(horizontal = 32.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Horário: ${evento.getHoraFormatada()}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Data: ${evento.getDataExtenso()}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = buildAnnotatedString {
                    append("Aproveite o ")
                    pushStyle(SpanStyle(color = Color(0xFFFFCC00), fontWeight = FontWeight.Bold))
                    append("Rolê!")
                },
                color = Color.White,
                fontSize = 16.sp
            )

            if (!cupomTitulo.isNullOrBlank()) {
                Spacer(Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .background(Color(0xFFFFCC00).copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cupom desbloqueado!",
                        color = Color(0xFFFFCC00),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cupomTitulo,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    if (!cupomDescricao.isNullOrBlank()) {
                        Text(
                            text = cupomDescricao,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (!cupomEstabelecimento.isNullOrBlank()) {
                        Text(
                            text = "Estabelecimento: $cupomEstabelecimento",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}