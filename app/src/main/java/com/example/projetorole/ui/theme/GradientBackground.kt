package com.example.projetorole.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F0FF),
                        Color(0xFFE3F0FF),
                        Color(0xFFE3F0FF).copy(alpha = 0.7f),
                        Color(0xFFFFE3F0).copy(alpha = 0.45f)
                    ),
                    startY = 0f,
                    endY = 1200f
                )
            )
    ) {
        content()
    }
}
