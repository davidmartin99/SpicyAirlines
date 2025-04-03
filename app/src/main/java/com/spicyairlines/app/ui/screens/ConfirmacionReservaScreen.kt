package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ConfirmacionReservaViewModel

@Composable
fun ConfirmacionReservaScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ConfirmacionReservaViewModel = viewModel(),
    onConfirmarClick: () -> Unit
) {
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val vuelo = sharedViewModel.vueloSeleccionado
    val clase = sharedViewModel.claseSeleccionada
    val pasajeros = sharedViewModel.pasajeros
    val total = sharedViewModel.precioTotal

    BasePantalla() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de la reserva")
            Text("Vuelo a: ${vuelo?.ciudadDestino}")
            Text("Clase: $clase")
            Text("Pasajeros: ${pasajeros.size}")
            Text("Total: $total €")

            if (cargando) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    cargando = true
                    viewModel.guardarReservaFirebase(
                        vuelo = vuelo!!,
                        clase = clase,
                        pasajeros = pasajeros,
                        precioTotal = total,
                        onSuccess = {
                            cargando = false
                            onConfirmarClick()
                        },
                        onFailure = {
                            cargando = false
                            error = "Error: ${it.message}"
                        }
                    )
                }) {
                    Text("Confirmar")
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
