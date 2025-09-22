package com.example.projetorole.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projetorole.ui.main.MainScreen
import com.example.projetorole.ui.feed.FeedScreen
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.ui.detail.DetalheEventoScreen
import com.example.projetorole.ui.salvos.CheckinsSalvosScreen
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import com.example.projetorole.ui.cupons.CuponsScreen
import com.example.projetorole.ui.conta.ContaScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    // ViewModels compartilhados
    val feedViewModel: FeedViewModel = viewModel()
    val checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel()
    val eventos by feedViewModel.eventos.collectAsState()
    
    MainScreen(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = "feed"
        ) {
            composable("feed") {
                FeedScreen(
                    onEventoClick = { evento ->
                        navController.navigate("detail/${evento.id}")
                    },
                    checkinsSalvosViewModel = checkinsSalvosViewModel
                )
            }
            
            composable(
                "detail/{eventoId}",
                arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventoId = backStackEntry.arguments?.getInt("eventoId") ?: 0
                
                val evento = eventos.find { it.id == eventoId }
                
                if (evento != null) {
                    DetalheEventoScreen(
                        evento = evento,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    PlaceholderScreen("Evento não encontrado")
                }
            }
            
            // Eventos salvos
            composable("checkin") {
                CheckinsSalvosScreen(
                    onEventoClick = { evento ->
                        navController.navigate("detail/${evento.id}")
                    },
                    checkinsSalvosViewModel = checkinsSalvosViewModel,
                    feedViewModel = feedViewModel
                )
            }
            
            composable("search") {
                PlaceholderScreen("Buscar")
            }
            
            composable("profile") {
                ContaScreen()
            }
            
            composable("cupons") {
                CuponsScreen()
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090040)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp
        )
    }
}