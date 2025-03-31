package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.cargarReservasUsuario()
    }

    val reservas by viewModel.reservas.collectAsState()

    BasePantalla(title = "Mis Reservas") {
        Column(modifier = Modifier.padding(16.dp)) {
            if (reservas.isEmpty()) {
                Text("No tienes reservas registradas.")
            } else {
                reservas.forEach { reservaConVuelo ->
                    val reserva = reservaConVuelo.reserva
                    val vuelo = reservaConVuelo.vuelo

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Destino: ${vuelo.destino}")
                            Text("Clase: ${reserva.clase}")
                            Text("Fecha ida: ${vuelo.fechaSalida}")
                            Text("Fecha vuelta: ${vuelo.fechaVuelta}")
                            Text("Precio total: ${reserva.precioTotal} €")
                        }
                    }
                }

            }
        }
    }
}
