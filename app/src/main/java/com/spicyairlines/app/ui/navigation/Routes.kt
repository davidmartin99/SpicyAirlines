package com.spicyairlines.app.navigation

sealed class Screen(val route: String) {
    object AuthInicio : Screen("authInicio")
    object Login : Screen("login")
    object Register : Screen("register")
    object Princiapl : Screen("principal")
    object Resultados : Screen("resultados")
    object VueloSeleccionado : Screen("vueloSeleccionado")
    object DatosPasajeros : Screen("datosPasajeros")
    object ConfirmacionReserva : Screen("confirmacionReserva")
    object PagoCompletado : Screen("pagoCompletado")
    object Perfil : Screen("perfil")
    object EditarPerfil : Screen("editarPerfil")
    object EditarPasajeros : Screen("editarPasajeros/{reservaId}")
}
