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
fun MainScreenEstabelecimento(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            content()
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
        ) {
            BottomBarEstabelecimento(
                currentRoute = currentRoute,
                onMyEventsClick = { 
                    navController.navigate("myEvents") {
                        popUpTo("myEvents") { inclusive = true }
                    }
                },
                onNewEventClick = {
                    navController.navigate("newEvent")
                },
                onProfileClick = { 
                    navController.navigate("profileEstab") 
                }
            )
        }
    }
}

@Composable
private fun BottomBarEstabelecimento(
    currentRoute: String?,
    onMyEventsClick: () -> Unit,
    onNewEventClick: () -> Unit,
    onProfileClick: () -> Unit
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
                icon = Icons.Default.List, 
                label = "Eventos", 
                isSelected = currentRoute == "myEvents",
                onClick = onMyEventsClick
            )
            BottomBarButton(
                icon = Icons.Default.Add,
                label = "Novo",
                isSelected = currentRoute == "newEvent",
                onClick = onNewEventClick
            )
            BottomBarButton(
                icon = Icons.Default.AccountCircle, 
                label = "Perfil", 
                isSelected = currentRoute == "profileEstab",
                onClick = onProfileClick
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