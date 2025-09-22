package com.example.projetorole.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Conteúdo das telas (com padding para o bottom bar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            content()
        }
        
        // Bottom Bar fixo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
        ) {
            BottomBar(
                currentRoute = currentRoute,
                onHomeClick = { 
                    navController.navigate("feed") {
                        popUpTo("feed") { inclusive = true }
                    }
                },
                onCuponsClick = {
                    navController.navigate("cupons") // ← ATUALIZADO
                },
                onCheckinClick = { 
                    navController.navigate("checkin") 
                },
                onContaClick = { 
                    navController.navigate("profile") 
                }
            )
        }
    }
}

@Composable
private fun BottomBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onCuponsClick: () -> Unit,
    onCheckinClick: () -> Unit,
    onContaClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF471396))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarButton(
                icon = Icons.Default.Home, 
                label = "Home", 
                isSelected = currentRoute == "feed",
                onClick = onHomeClick
            )
            BottomBarButton(
                icon = Icons.Default.Star, // ← MUDADO DE LocalOffer PARA Star
                label = "Cupons",
                isSelected = currentRoute == "cupons",
                onClick = onCuponsClick
            )
            BottomBarButton(
                icon = Icons.Default.Favorite, // ← MUDADO DE CheckCircle PARA Favorite
                label = "Check-In", // ← MANTIDO
                isSelected = currentRoute == "checkin",
                onClick = onCheckinClick
            )
            BottomBarButton(
                icon = Icons.Default.AccountCircle, 
                label = "Conta", 
                isSelected = currentRoute == "profile",
                onClick = onContaClick
            )
        }
    }
}

@Composable
private fun BottomBarButton(
    icon: ImageVector, 
    label: String, 
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFFFFCC00) else Color(0xFFFEF7FF),
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color(0xFFFFCC00) else Color(0xFFFEF7FF),
            fontSize = 12.sp
        )
    }
}