package com.example.projetorole.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projetorole.data.model.Evento
import com.example.projetorole.ui.feed.FeedScreen
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.ui.detail.DetalheEventoScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val feedViewModel: FeedViewModel = viewModel()
    val eventos by feedViewModel.eventos.collectAsState()

    NavHost(navController, startDestination = "feed") {
        composable("feed") {
            FeedScreen(
                eventos = eventos,
                onEventoClick = { evento ->
                    navController.navigate("detail/${evento.id}")
                }
            )
        }
        composable(
            "detail/{eventoId}",
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getInt("eventoId")
            val evento = eventos.find { it.id == eventoId }
            evento?.let {
                DetalheEventoScreen(
                    evento = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}