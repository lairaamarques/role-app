package com.example.projetorole.ui.cupons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupomFormScreen(
    viewModel: CupomFormViewModel = viewModel(),
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            snackbarHostState.showSnackbar("Cupom criado com sucesso!")
            viewModel.success = false
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Novo Cupom") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Crie um benefício para seus clientes",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = viewModel.titulo,
                onValueChange = { viewModel.titulo = it },
                label = { Text("Título (ex: 10% OFF)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color(0xFF090040),
                    unfocusedLabelColor = Color.Gray
                ),
                enabled = !viewModel.isLoading
            )

            OutlinedTextField(
                value = viewModel.descricao,
                onValueChange = { viewModel.descricao = it },
                label = { Text("Regras / Descrição") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color(0xFF090040),
                    unfocusedLabelColor = Color.Gray
                ),
                enabled = !viewModel.isLoading
            )

            Spacer(Modifier.height(16.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Button(
                    onClick = { viewModel.criarCupom() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Criar Cupom",
                        color = Color(0xFF090040),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}