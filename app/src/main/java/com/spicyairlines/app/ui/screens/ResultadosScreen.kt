package com.spicyairlines.app.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.ui.utils.HoraUTC.formatearFechaHoraLocal
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ResultadosScreen(
    sharedViewModel: SharedViewModel,
    resultadosViewModel: ResultadosViewModel,
    onSeleccionarVuelo: () -> Unit,
    onBack: () -> Unit,
    onPerfilClick: () -> Unit
) {
    val vuelosIda by resultadosViewModel.vuelosIda.collectAsState()
    val vuelosVuelta by resultadosViewModel.vuelosVuelta.collectAsState()
    val combinacionesValidas by resultadosViewModel.combinacionesValidas.collectAsState()
    val cargaCompletada by resultadosViewModel.cargaCompletada.collectAsState()

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (vuelosVuelta.isEmpty()) "Vuelos disponibles" else "Combinaciones de vuelos",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                !cargaCompletada -> {
                    CircularProgressIndicator()
                }

                vuelosIda.isEmpty() && vuelosVuelta.isEmpty() -> {
                    Text("❌ No se encontraron vuelos disponibles.")
                }

                vuelosVuelta.isNotEmpty() && combinacionesValidas.isEmpty() -> {
                    Text("❌ No se encontraron combinaciones válidas.")
                }

                vuelosVuelta.isEmpty() -> {
                    vuelosIda.forEach { vuelo ->
                        VueloCard(vuelo = vuelo, sharedViewModel = sharedViewModel) {
                            sharedViewModel.seleccionarVuelo(vuelo)
                            sharedViewModel.seleccionarVueloVuelta(null)
                            sharedViewModel.calcularPrecioBillete(vuelo) // Guarda el precio por billete
                            onSeleccionarVuelo()
                        }
                    }
                }

                else -> {
                    combinacionesValidas.forEach { (ida, vuelta) ->
                        VueloCombinadoCard(ida, vuelta, sharedViewModel = sharedViewModel) {
                            sharedViewModel.seleccionarVuelo(ida)
                            sharedViewModel.seleccionarVueloVuelta(vuelta)
                            sharedViewModel.calcularPrecioBillete(ida, vuelta) // Guarda el precio por billete
                            onSeleccionarVuelo()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun VueloCard(vuelo: Vuelo, sharedViewModel: SharedViewModel, onClick: () -> Unit) {
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val multiplicador = when (clase) {
        "Premium" -> 1.5
        "Business" -> 2.0
        else -> 1.0
    }
    val precioPorPasajero = vuelo.precioBase * multiplicador

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✈️ Vuelo disponible", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${vuelo.origen} (${vuelo.aeropuertoOrigen}) → ${vuelo.destino} (${vuelo.aeropuertoDestino})", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${formatearFechaHoraLocal(vuelo.fechaSalida, vuelo.origen)}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${formatearFechaHoraLocal(vuelo.fechaLlegada, vuelo.destino)}", style = MaterialTheme.typography.bodySmall)
            Text("Duración: ${vuelo.duracion}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("💰 Precio por billete: $precioPorPasajero €", style = MaterialTheme.typography.titleMedium)
        }
    }
}



@Composable
fun VueloCombinadoCard(ida: Vuelo, vuelta: Vuelo, sharedViewModel: SharedViewModel, onClick: () -> Unit) {
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val multiplicador = when (clase) {
        "Premium" -> 1.5
        "Business" -> 2.0
        else -> 1.0
    }
    val precioPorPasajero = (ida.precioBase + vuelta.precioBase) * multiplicador

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✈️ Vuelo de ida", style = MaterialTheme.typography.titleMedium)
            Text("${ida.origen} (${ida.aeropuertoOrigen}) → ${ida.destino} (${ida.aeropuertoDestino})", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${formatearFechaHoraLocal(ida.fechaSalida, ida.origen)}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${formatearFechaHoraLocal(ida.fechaLlegada, ida.destino)}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Text("🔁 Vuelo de vuelta", style = MaterialTheme.typography.titleMedium)
            Text("${vuelta.origen} (${vuelta.aeropuertoOrigen}) → ${vuelta.destino} (${vuelta.aeropuertoDestino})", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${formatearFechaHoraLocal(vuelta.fechaSalida, vuelta.origen)}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${formatearFechaHoraLocal(vuelta.fechaLlegada, vuelta.destino)}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("💰 Precio por billete: $precioPorPasajero €", style = MaterialTheme.typography.titleMedium)
        }
    }
}





