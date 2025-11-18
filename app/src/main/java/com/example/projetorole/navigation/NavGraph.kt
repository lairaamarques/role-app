package com.example.projetorole.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.projetorole.data.auth.ActorType
import com.example.projetorole.data.auth.AuthRepository
import com.example.projetorole.ui.auth.AuthOptionsScreen
import com.example.projetorole.ui.auth.LoginEstabelecimentoScreen
import com.example.projetorole.ui.auth.LoginUsuarioScreen
import com.example.projetorole.ui.auth.RegisterEstabelecimentoScreen
import com.example.projetorole.ui.auth.RegisterUsuarioScreen
import com.example.projetorole.ui.conta.ContaScreen
import com.example.projetorole.ui.conta.ContaViewModel
import com.example.projetorole.ui.cupons.CuponsScreen
import com.example.projetorole.ui.detail.CheckinSuccessScreen
import com.example.projetorole.ui.detail.DetalheEventoScreen
import com.example.projetorole.ui.feed.FeedScreen
import com.example.projetorole.ui.feed.FeedViewModel
import com.example.projetorole.ui.main.MainScreen
import com.example.projetorole.ui.main.MainScreenEstabelecimento
import com.example.projetorole.ui.manage.EventFormScreen
import com.example.projetorole.ui.manage.EventFormViewModel
import com.example.projetorole.ui.manage.MyEventsScreen
import com.example.projetorole.ui.manage.MyEventsViewModel
import com.example.projetorole.ui.salvos.CheckinsSalvosScreen
import com.example.projetorole.ui.salvos.CheckinsSalvosViewModel
import com.example.projetorole.ui.cupons.CupomFormScreen
import com.example.projetorole.ui.cupons.CupomFormViewModel

@Composable
fun NavGraph() {
    val token by AuthRepository.token.collectAsState()
    val actorType by AuthRepository.actorType.collectAsState()

    LaunchedEffect(Unit) {
        AuthRepository.authEvents.collect { event ->
            if (event == AuthRepository.AuthEvent.Unauthorized) {
                AuthRepository.clearToken()
            }
        }
    }
    if (token.isNullOrBlank()) {
        AuthNavHost()
    } else {
        val isEstabelecimento = actorType == ActorType.ESTAB
        val navController = rememberNavController()
        val feedViewModel: FeedViewModel = viewModel()
        val checkinsSalvosViewModel: CheckinsSalvosViewModel = viewModel()
        val eventos by feedViewModel.eventos.collectAsState()

        if (isEstabelecimento) {
            MainScreenEstabelecimento(navController = navController) {
                NavHost(navController = navController, startDestination = "myEvents") {
                    composable("myEvents") { backStackEntry ->
                        val manageViewModel: MyEventsViewModel = viewModel()
                        val refresh by backStackEntry.savedStateHandle
                            .getStateFlow("refresh", false)
                            .collectAsState()

                        LaunchedEffect(refresh) {
                            if (refresh) {
                                manageViewModel.loadEventos()
                                backStackEntry.savedStateHandle["refresh"] = false
                            }
                        }
                        MyEventsScreen(
                            viewModel = manageViewModel,
                            onBack = { navController.popBackStack() },
                            onCreateEvent = { navController.navigate("newEvent") },
                            onEditEvent = { evento ->
                                navController.navigate("eventForm?eventoId=${evento.id}")
                            }
                        )
                    }

                    composable("newEvent") {
                        val formViewModel: EventFormViewModel = viewModel()
                        EventFormScreen(
                            viewModel = formViewModel,
                            eventoId = null,
                            onBack = { navController.popBackStack() },
                            onSuccess = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("refresh", true)
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("profileEstab") {
                        ContaScreen(
                            isEstabelecimento = true,
                            onManageEvents = { navController.navigate("myEvents") },
                            onCuponsClick = { navController.navigate("cupons") },
                            contaViewModel = ContaViewModel(navController)
                        )
                    }

                    composable("cupons") {
                        CuponsScreen(
                            isEstabelecimento = true,
                            onCreateCupom = {
                                navController.navigate("cupomForm")
                            }
                        )
                    }

                    composable("cupomForm") {
                        val cupomViewModel: com.example.projetorole.ui.cupons.CupomFormViewModel = viewModel()
                        com.example.projetorole.ui.cupons.CupomFormScreen(
                            viewModel = cupomViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        "eventForm?eventoId={eventoId}",
                        arguments = listOf(
                            navArgument("eventoId") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) { backStackEntry ->
                        val eventoId = backStackEntry.arguments
                            ?.getInt("eventoId")
                            ?.takeIf { it > 0 }

                        val formViewModel: EventFormViewModel = viewModel()
                        EventFormScreen(
                            viewModel = formViewModel,
                            eventoId = eventoId,
                            onBack = { navController.popBackStack() },
                            onSuccess = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("refresh", true)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        } else {
            MainScreen(navController = navController) {
                NavHost(navController = navController, startDestination = "feed") {
                    composable("feed") {
                        FeedScreen(
                            onEventoClick = { evento -> navController.navigate("detail/${evento.id}") },
                            checkinsSalvosViewModel = checkinsSalvosViewModel
                        )
                    }
                    composable(
                        "detail/{eventoId}",
                        arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
                    ) { entry ->
                        val eventoId = entry.arguments?.getInt("eventoId")

                        if (eventoId == null || eventoId == 0) {
                            PlaceholderScreen("ID do evento inválido")
                        } else {
                            DetalheEventoScreen(
                                eventoId = eventoId,

                                onBack = { navController.popBackStack() },

                                onCheckinSuccess = {
                                    navController.navigate("checkinSuccess/${eventoId}") {
                                        launchSingleTop = true
                                        popUpTo("feed")
                                    }
                                }
                            )
                        }
                    }

                    composable(
                        route = "checkinSuccess/{eventoId}",
                        arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
                    ) { entry ->
                        val eventoId = entry.arguments?.getInt("eventoId") ?: return@composable
                        val evento = eventos.find { it.id == eventoId }
                        if (evento != null) {
                            CheckinSuccessScreen(
                                evento = evento,
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            PlaceholderScreen("Evento não encontrado")
                        }
                    }

                    composable("checkin") {
                        CheckinsSalvosScreen(
                            onEventoClick = { evento -> navController.navigate("detail/${evento.id}") },
                            checkinsSalvosViewModel = checkinsSalvosViewModel,
                            feedViewModel = feedViewModel
                        )
                    }

                    composable("cupons") {
                        val isEstab = actorType == ActorType.ESTAB
                        CuponsScreen(
                            isEstabelecimento = isEstab,
                            onCreateCupom = {
                                navController.navigate("cupomForm")
                            }
                        )
                    }
                    composable("profile") {
                        ContaScreen(
                            isEstabelecimento = false,
                            onManageEvents = {},
                            onCuponsClick = { navController.navigate("cupons") },
                            contaViewModel = ContaViewModel(navController)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "authOptions") {
        composable("authOptions") {
            AuthOptionsScreen(
                onUsuarioClick = { navController.navigate("loginUsuario") },
                onEstabelecimentoClick = { navController.navigate("loginEstabelecimento") }
            )
        }
        composable("loginUsuario") {
            LoginUsuarioScreen(
                onNavigateToRegister = { navController.navigate("registerUsuario") },
                onSwitchAccount = {
                    navController.navigate("loginEstabelecimento") {
                        popUpTo("authOptions")
                    }
                },
                onLoginSuccess = {
                    navController.popBackStack(route = "authOptions", inclusive = false)
                }
            )
        }
        composable("loginEstabelecimento") {
            LoginEstabelecimentoScreen(
                onNavigateToRegister = { navController.navigate("registerEstabelecimento") },
                onSwitchAccount = {
                    navController.navigate("loginUsuario") {
                        popUpTo("authOptions")
                    }
                },
                onLoginSuccess = {
                    navController.popBackStack(route = "authOptions", inclusive = false)
                }
            )
        }
        composable("registerUsuario") {
            RegisterUsuarioScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack(route = "loginUsuario", inclusive = false)
                }
            )
        }
        composable("registerEstabelecimento") {
            RegisterEstabelecimentoScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack(route = "loginEstabelecimento", inclusive = false)
                }
            )
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
        Text(text = title, color = Color.White, fontSize = 24.sp)
    }
}