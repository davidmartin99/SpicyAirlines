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
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel

@Composable
fun ResultadosScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ResultadosViewModel = viewModel(),
    onSeleccionarVuelo: () -> Unit,
    onBack: () -> Unit,
    onPerfilClick: () -> Unit
) {
    val vuelosIda by viewModel.vuelosIda.collectAsState()
    val vuelosVuelta by viewModel.vuelosVuelta.collectAsState()

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (vuelosVuelta.isEmpty()) "Vuelos disponibles" else "Combinaciones de vuelos",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (vuelosIda.isEmpty()) {
                Text("No se encontraron vuelos disponibles.")
            } else if (vuelosVuelta.isEmpty()) {
                vuelosIda.forEach { vuelo ->
                    VueloCard(vuelo = vuelo) {
                        sharedViewModel.seleccionarVuelo(vuelo)
                        sharedViewModel.seleccionarVueloVuelta(null)
                        onSeleccionarVuelo()
                    }
                }
            } else {
                vuelosIda.forEach { ida ->
                    vuelosVuelta.forEach { vuelta ->
                        VueloCombinadoCard(ida, vuelta) {
                            sharedViewModel.seleccionarVuelo(ida)
                            sharedViewModel.seleccionarVueloVuelta(vuelta)
                            onSeleccionarVuelo()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VueloCard(vuelo: Vuelo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Origen: ${vuelo.origen}")
            Text("Destino: ${vuelo.destino}")
            Text("Salida: ${vuelo.fechaSalida.toDate()}")
            Text("Llegada: ${vuelo.fechaLlegada.toDate()}")
            Text("Precio desde: ${vuelo.precioBase}€")
        }
    }
}

@Composable
fun VueloCombinadoCard(ida: Vuelo, vuelta: Vuelo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🛫 Ida: ${ida.origen} → ${ida.destino}")
            Text("   Salida: ${ida.fechaSalida.toDate()}")
            Text("   Llegada: ${ida.fechaLlegada.toDate()}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("🛬 Vuelta: ${vuelta.origen} → ${vuelta.destino}")
            Text("   Salida: ${vuelta.fechaSalida.toDate()}")
            Text("   Llegada: ${vuelta.fechaLlegada.toDate()}")

            Spacer(modifier = Modifier.height(8.dp))

            val total = ida.precioBase + vuelta.precioBase
            Text("💰 Precio total desde: $total €")
        }
    }
}
