package com.example.projetorole.ui.conta

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.projetorole.R
import com.example.projetorole.data.auth.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authProfile by AuthRepository.profile.collectAsState()
    var nome by remember { mutableStateOf(authProfile?.displayName ?: "") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> selectedUri = uri }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Editar perfil", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090040))
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else if (!authProfile?.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = authProfile!!.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.tower_avatar_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(Modifier.height(12.dp))
            Button(onClick = { picker.launch("image/*") }) { Text("Escolher imagem") }
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nome de exibição") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF090040),
                    unfocusedTextColor = Color(0xFF090040)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.submitProfile(context, nome.takeIf { it.isNotBlank() }, selectedUri) { ok, err ->
                        if (ok) {
                            Toast.makeText(context, "Perfil atualizado", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, err ?: "Erro", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold)
            }
        }
    }
}