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
    onConfirmarClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val vueloIda by sharedViewModel.vueloSeleccionado.collectAsState()
    val vueloVuelta by sharedViewModel.vueloVueltaSeleccionado.collectAsState()
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val pasajeros by sharedViewModel.pasajeros.collectAsState()
    val total by sharedViewModel.precioTotal.collectAsState()

    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (vueloIda == null) {
        BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No se ha seleccionado un vuelo.")
            }
        }
        return
    }

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de la reserva", style = MaterialTheme.typography.titleMedium)

            // Vuelo de ida
            Text("✈️ Ida: ${vueloIda!!.origen} → ${vueloIda!!.destino}")
            Text("   Salida: ${vueloIda!!.fechaSalida.toDate()}")
            Text("   Llegada: ${vueloIda!!.fechaLlegada.toDate()}")

            // Vuelo de vuelta (opcional)
            vueloVuelta?.let {
                Text("🛬 Vuelta: ${it.origen} → ${it.destino}")
                Text("   Salida: ${it.fechaSalida.toDate()}")
                Text("   Llegada: ${it.fechaLlegada.toDate()}")
            }

            Text("Clase: $clase")
            Text("Pasajeros: ${pasajeros.size}")
            Text("Total: $total €")

            if (cargando) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        cargando = true
                        viewModel.guardarReservaFirebase(
                            vueloIda = vueloIda!!,
                            vueloVuelta = vueloVuelta,
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
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar")
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
