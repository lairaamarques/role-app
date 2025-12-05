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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetorole.R
import com.example.projetorole.data.model.Evento
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun CheckinFarScreen(
    evento: Evento,
    timestampMillis: Long,
    onBack: () -> Unit
) {
    val formatted = runCatching {
        val instant = Instant.ofEpochMilli(timestampMillis)
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }.getOrDefault("")

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
                    val target = if (i % 2 == 0) -20f else 20f
                    shakeOffset.animateTo(target, animationSpec = tween(durationMillis = 60))
                }
                shakeOffset.animateTo(0f, animationSpec = tween(durationMillis = 120))
            }

            Image(
                painter = painterResource(id = R.drawable.cancel_icon),
                contentDescription = "Longe do evento",
                modifier = Modifier
                    .size(180.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Longe do evento",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Se aproxime para fazer o check‑in",
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
        }
    }
}