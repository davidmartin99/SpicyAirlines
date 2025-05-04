package com.spicyairlines.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.ui.utils.HoraUTC.formatearFechaHoraLocal
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel


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

    var ordenPrecio by remember { mutableStateOf("Menor a mayor") }
    val opcionesOrden = listOf("Menor a mayor", "Mayor a menor")

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🔽 Título y dropdown para ordenar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (vuelosVuelta.isEmpty()) "Vuelos disponibles" else "Combinaciones de vuelos",
                    style = MaterialTheme.typography.titleLarge
                )

                var expanded by remember { mutableStateOf(false) }

                Box {
                    Button(onClick = { expanded = true }) {
                        Text("Orden: $ordenPrecio")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        opcionesOrden.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    ordenPrecio = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                !cargaCompletada -> {
                    CircularProgressIndicator()
                }

                vuelosIda.isEmpty() -> {
                    Text("❌ No se encontraron vuelos disponibles.")
                }

                vuelosVuelta.isEmpty() -> {
                    val vuelosOrdenados = ordenarVuelosIda(vuelosIda, ordenPrecio)
                    vuelosOrdenados.forEach { vuelo ->
                        VueloCard(vuelo = vuelo, sharedViewModel = sharedViewModel) {
                            sharedViewModel.seleccionarVuelo(vuelo)
                            sharedViewModel.seleccionarVueloVuelta(null)
                            sharedViewModel.calcularPrecioBillete(vuelo)
                            onSeleccionarVuelo()
                        }
                    }
                }

                else -> {
                    val combinacionesOrdenadas = ordenarCombinaciones(combinacionesValidas, ordenPrecio)
                    combinacionesOrdenadas.forEach { (ida, vuelta) ->
                        VueloCombinadoCard(ida, vuelta, sharedViewModel = sharedViewModel) {
                            sharedViewModel.seleccionarVuelo(ida)
                            sharedViewModel.seleccionarVueloVuelta(vuelta)
                            sharedViewModel.calcularPrecioBillete(ida, vuelta)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorPorTemporada(vuelo.temporada))
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorPorTemporada(ida.temporada))
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

// ✅ Función auxiliar para ordenar vuelos de ida
fun ordenarVuelosIda(vuelos: List<Vuelo>, orden: String): List<Vuelo> {
    return when (orden) {
        "Mayor a menor" -> vuelos.sortedByDescending { it.precioBase }
        else -> vuelos.sortedBy { it.precioBase }
    }
}

// ✅ Función auxiliar para ordenar combinaciones ida + vuelta
fun ordenarCombinaciones(combinaciones: List<Pair<Vuelo, Vuelo>>, orden: String): List<Pair<Vuelo, Vuelo>> {
    return when (orden) {
        "Mayor a menor" -> combinaciones.sortedByDescending { it.first.precioBase + it.second.precioBase }
        else -> combinaciones.sortedBy { it.first.precioBase + it.second.precioBase }
    }
}

// 🎨 Asigna color según temporada
fun colorPorTemporada(temporada: String): Color {
    return when (temporada.lowercase()) {
        "alta" -> Color(0xFF65010C)
        "media" -> Color(0xFF9A5E02) // naranja oscuro
        "baja" -> Color(0xFF07490A)  // verde oscuro
        else -> Color(0xFFFFFFFF)
    }
}
