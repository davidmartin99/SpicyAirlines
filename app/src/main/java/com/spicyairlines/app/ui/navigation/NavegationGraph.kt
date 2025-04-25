package com.spicyairlines.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spicyairlines.app.screens.*
import com.spicyairlines.app.ui.screens.EditarPerfilScreen
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.EditarPerfilViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel

sealed class Screen(val route: String) {
    object AuthInicio : Screen("authInicio")
    object Login : Screen("login")
    object Register : Screen("register")
    object Inicio : Screen("inicio")
    object Resultados : Screen("resultados")
    object VueloSeleccionado : Screen("vueloSeleccionado")
    object DatosPasajeros : Screen("datosPasajeros")
    object ConfirmacionReserva : Screen("confirmacionReserva")
    object PagoCompletado : Screen("pagoCompletado")
    object Perfil : Screen("perfil")
    object EditarPerfil : Screen("editarPerfil")

}


@Composable
fun NavigationGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    resultadosViewModel: ResultadosViewModel = viewModel()

) {
    NavHost(navController = navController, startDestination = Screen.AuthInicio.route) {

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

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.AuthInicio.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Inicio.route) {
            InicioScreen(
                sharedViewModel = sharedViewModel,
                resultadosViewModel = resultadosViewModel,
                onBuscarClick = {
                    navController.navigate(Screen.Resultados.route)
                },
                onPerfilClick = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }


        composable(Screen.Resultados.route) {
            ResultadosScreen(
                sharedViewModel = sharedViewModel, // ✅ también importante
                resultadosViewModel = resultadosViewModel,
                onSeleccionarVuelo = {
                    navController.navigate(Screen.VueloSeleccionado.route)
                },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }


        composable(Screen.VueloSeleccionado.route) {
            VueloSeleccionadoScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = {
                    navController.navigate(Screen.DatosPasajeros.route)
                },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.DatosPasajeros.route) {
            DatosPasajerosScreen(
                sharedViewModel = sharedViewModel,
                onContinuarClick = {
                    navController.navigate(Screen.ConfirmacionReserva.route)
                },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        composable(Screen.ConfirmacionReserva.route) {
            ConfirmacionReservaScreen(
                sharedViewModel = sharedViewModel,
                onConfirmarClick = {
                    navController.navigate(Screen.PagoCompletado.route)
                },
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
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
                },
                onBack = { navController.popBackStack() },
                onEditarPerfil = {
                    navController.navigate(Screen.EditarPerfil.route) // ✅ Navegación a nueva pantalla
                }
            )
        }

        composable(Screen.EditarPerfil.route) {
            val editarPerfilViewModel: EditarPerfilViewModel = viewModel()
            EditarPerfilScreen(
                viewModel = editarPerfilViewModel,
                onBack = { navController.popBackStack() }
            )
        }


    }
}


