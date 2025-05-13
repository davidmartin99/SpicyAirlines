package com.spicyairlines.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spicyairlines.app.screens.*
import com.spicyairlines.app.ui.screens.EditarPasajerosScreen
import com.spicyairlines.app.ui.screens.EditarPerfilScreen
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.EditarPerfilViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    resultadosViewModel: ResultadosViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Screen.AuthInicio.route) {

        // --- AUTH ---
        composable(Screen.AuthInicio.route) {
            PantallaInicioAuth(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Princiapl.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Princiapl.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- INICIO Y RESULTADOS ---
        composable(Screen.Princiapl.route) {
            PrincipalScreen(
                sharedViewModel = sharedViewModel,
                resultadosViewModel = resultadosViewModel,
                onBuscarClick = { navController.navigate(Screen.Resultados.route) },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.Resultados.route) {
            ResultadosScreen(
                sharedViewModel = sharedViewModel,
                resultadosViewModel = resultadosViewModel,
                onSeleccionarVuelo = { navController.navigate(Screen.VueloSeleccionado.route) },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // --- FLUJO DE RESERVA ---
        composable(Screen.VueloSeleccionado.route) {
            VueloSeleccionadoScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = { navController.navigate(Screen.DatosPasajeros.route) },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.DatosPasajeros.route) {
            DatosPasajerosScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = { navController.navigate(Screen.ConfirmacionReserva.route) },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.ConfirmacionReserva.route) {
            ConfirmacionReservaScreen(
                sharedViewModel = sharedViewModel,
                onConfirmarClick = { navController.navigate(Screen.PagoCompletado.route) },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.PagoCompletado.route) {
            PagoCompletadoScreen(
                onVolverInicio = {
                    navController.navigate(Screen.Princiapl.route) {
                        popUpTo(Screen.Princiapl.route) { inclusive = true }
                    }
                }
            )
        }

        // --- PERFIL ---
        composable(Screen.Perfil.route) {
            PerfilScreen(
                navController = navController,
                onCerrarSesion = {
                    navController.navigate(Screen.AuthInicio.route) {
                        popUpTo(Screen.Princiapl.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onEditarPerfil = { navController.navigate(Screen.EditarPerfil.route) }
            )
        }

        composable(Screen.EditarPerfil.route) {
            EditarPerfilScreen(
                viewModel = viewModel<EditarPerfilViewModel>(),
                onBack = { navController.popBackStack() }
            )
        }

        // --- EDICIÓN PASAJEROS ---
        composable(Screen.EditarPasajeros.route) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getString("reservaId").orEmpty()
            EditarPasajerosScreen(
                navController = navController,
                reservaId = reservaId
            )
        }
    }
}
