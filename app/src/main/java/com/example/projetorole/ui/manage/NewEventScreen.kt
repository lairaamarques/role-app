package com.example.projetorole.ui.manage

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Novo evento") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nome do evento", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                colors = eventFormColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Local", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                colors = eventFormColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Horário (ex: 2025-11-05T19:00)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                colors = eventFormColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                placeholder = { Text("Descrição (opcional)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                colors = eventFormColors()
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFFCC00),
                        uncheckedColor = Color.White.copy(alpha = 0.4f)
                    )
                )
                Text("Evento pago", color = Color.White, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCC00),
                    contentColor = Color(0xFF090040)
                )
            ) {
                Text("Salvar evento", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun eventFormColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
    focusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    unfocusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    focusedTextColor = Color(0xFF090040),
    unfocusedTextColor = Color(0xFF090040),
    cursorColor = Color(0xFF090040)
)