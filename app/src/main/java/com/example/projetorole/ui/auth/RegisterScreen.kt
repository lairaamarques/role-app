package com.example.projetorole.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
            onRegisterSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090040))
                .padding(padding)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                val title = buildAnnotatedString {
                    append("Crie sua conta no ")
                    pushStyle(SpanStyle(color = Color(0xFFFFCC00)))
                    append("Rolê")
                    pop()
                }

                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Participe dos melhores eventos da cidade.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = viewModel::onNomeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Seu nome", color = Color(0xFFB0B0B0))
                    },
                    singleLine = true,
                    colors = outlinedColors()
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Email:", color = Color.White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("exemplo@gmail.com", color = Color(0xFFB0B0B0))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = outlinedColors()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Senha:", color = Color.White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = viewModel::onSenhaChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Mínimo de 6 caracteres", color = Color(0xFFB0B0B0))
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = outlinedColors()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Confirmar senha:", color = Color.White, fontSize = 14.sp)
                    OutlinedTextField(
                        value = uiState.confirmarSenha,
                        onValueChange = viewModel::onConfirmarSenhaChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Repita a senha", color = Color(0xFFB0B0B0))
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = outlinedColors()
                    )
                }

                Button(
                    onClick = viewModel::registrar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCC00),
                        contentColor = Color(0xFF090040)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF090040),
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text("Criar conta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                TextButton(onClick = onBack) {
                    Text("Já tenho uma conta", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun outlinedColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
    containerColor = Color.White,
    focusedTextColor = Color(0xFF090040),
    unfocusedTextColor = Color(0xFF090040),
    cursorColor = Color(0xFF090040),
    focusedPlaceholderColor = Color(0xFFB0B0B0),
    unfocusedPlaceholderColor = Color(0xFFB0B0B0)
)