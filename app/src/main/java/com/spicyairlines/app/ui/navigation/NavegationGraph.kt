package com.spicyairlines.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spicyairlines.app.screens.*
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.PerfilViewModel

sealed class Screen(val route: String) {
    object AuthInicio : Screen("authInicio")
    object Login : Screen("login")
    object Register : Screen("register")

    object Inicio : Screen("inicio")
    object Resultados : Screen("resultados")
    object SeleccionClase : Screen("seleccionClase")
    object DatosPasajeros : Screen("datosPasajeros")
    object ConfirmacionReserva : Screen("confirmacionReserva")
    object PagoCompletado : Screen("pagoCompletado")
    object Perfil : Screen("perfil")
}


@Composable
fun NavigationGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel

) {
    NavHost(navController = navController, startDestination = Screen.AuthInicio.route) {

        // Pantalla de bienvenida con botones de login/registro
        composable(Screen.AuthInicio.route) {
            PantallaInicioAuth(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack() // <- volver a la pantalla anterior
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantallas de reserva
        composable(Screen.Inicio.route) {
            InicioScreen(
                onBuscarClick = {
                    navController.navigate(Screen.Resultados.route)
                }
            )
        }

        composable(Screen.Resultados.route) {
            ResultadosScreen(
                onVueloSeleccionado = {
                    navController.navigate(Screen.SeleccionClase.route)
                }
            )
        }

        composable(Screen.SeleccionClase.route) {
            SeleccionClaseScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = {
                    navController.navigate(Screen.DatosPasajeros.route)
                }
            )
        }


        composable(Screen.DatosPasajeros.route) {
            DatosPasajerosScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = {
                    navController.navigate(Screen.ConfirmacionReserva.route)
                }
            )
        }


        composable(Screen.ConfirmacionReserva.route) {
            ConfirmacionReservaScreen(
                sharedViewModel = sharedViewModel,
                onConfirmarClick = {
                    navController.navigate(Screen.PagoCompletado.route)
                }
            )
        }

        composable(Screen.PagoCompletado.route) {
            PagoCompletadoScreen(
                onVolverInicio = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                onCerrarSesion = {
                    navController.navigate(Screen.AuthInicio.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

    }
}
