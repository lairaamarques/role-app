package com.example.projetorole.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterEstabelecimentoScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterEstabelecimentoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHost.showSnackbar(it); viewModel.clearError() }
    }
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.consumeSuccess()
            snackbarHost.showSnackbar("Estabelecimento cadastrado!")
            onRegisterSuccess()
        }
    }

    androidx.compose.material3.Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val title = buildAnnotatedString {
                append("Cadastrar parceiro no ")
                pushStyle(SpanStyle(color = Color(0xFFFFCC00)))
                append("Rolê")
                pop()
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Complete os dados para continuar.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.nomeFantasia,
                onValueChange = viewModel::onNomeFantasiaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nome fantasia", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                singleLine = true,
                colors = outlinedColors(),
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("E-mail", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = outlinedColors(),
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.senha,
                onValueChange = viewModel::onSenhaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Senha", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                colors = outlinedColors(),
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.confirmarSenha,
                onValueChange = viewModel::onConfirmarSenhaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Confirmar senha", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = outlinedColors(),
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.cnpj,
                onValueChange = viewModel::onCnpjChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("CNPJ (opcional)", color = Color(0xFF090040).copy(alpha = 0.6f)) },
                singleLine = true,
                colors = outlinedColors(),
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::registrar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00), contentColor = Color(0xFF090040))
            ) {
                Text(if (uiState.isLoading) "Enviando..." else "Cadastrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = onBack, enabled = !uiState.isLoading) {
                Text("Já tenho uma conta", color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun outlinedColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White.copy(alpha = 0.4f),
    focusedLabelColor = Color(0xFF090040),
    unfocusedLabelColor = Color(0xFF090040),
    focusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    unfocusedPlaceholderColor = Color(0xFF090040).copy(alpha = 0.6f),
    cursorColor = Color(0xFF090040),
    focusedTextColor = Color(0xFF090040),
    unfocusedTextColor = Color(0xFF090040)
)