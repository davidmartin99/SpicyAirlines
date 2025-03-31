package com.spicyairlines.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.viewmodel.ResultadosViewModel

@Composable
fun ResultadosScreen(
    onVueloSeleccionado: (Vuelo) -> Unit,
    viewModel: ResultadosViewModel = viewModel()
) {
    val destino = viewModel.destino
    val fechaIda = viewModel.fechaIda
    val fechaVuelta = viewModel.fechaVuelta

    LaunchedEffect(destino, fechaIda, fechaVuelta) {
        viewModel.cargarVuelos(destino, fechaIda, fechaVuelta)
    }

    val vuelos by viewModel.vuelos.collectAsState()

    BasePantalla(title = "Resultados de Vuelos") {
        Column(modifier = Modifier.padding(16.dp)) {
            if (vuelos.isEmpty()) {
                Text("No se encontraron vuelos.")
            } else {
                vuelos.sortedBy { it.precioBase }.forEach { vuelo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onVueloSeleccionado(vuelo) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Destino: ${vuelo.destino}")
                            Text("Fecha ida: ${vuelo.fechaSalida}")
                            Text("Fecha vuelta: ${vuelo.fechaVuelta}")
                            Text("Precio base: ${vuelo.precioBase}€")
                        }
                    }
                }
            }
        }
    }
}
