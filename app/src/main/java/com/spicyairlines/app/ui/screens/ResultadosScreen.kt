package com.spicyairlines.app.screens

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
                vuelosIda.isEmpty() -> {
                    Text("❌ No se encontraron vuelos disponibles.")
                }

                vuelosVuelta.isEmpty() -> {
                    vuelosIda.forEach { vuelo ->
                        VueloCard(vuelo = vuelo) {
                            sharedViewModel.seleccionarVuelo(vuelo)
                            sharedViewModel.seleccionarVueloVuelta(null)
                            onSeleccionarVuelo()
                        }
                    }
                }

                combinacionesValidas.isEmpty() -> {
                    Text("❌ No se encontraron combinaciones válidas.")
                }

                else -> {
                    combinacionesValidas.forEach { (ida, vuelta) ->
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
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✈️ Vuelo disponible", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${vuelo.origen} → ${vuelo.destino}", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${vuelo.fechaSalida.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${vuelo.fechaLlegada.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Duración: ${vuelo.duracion}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("💰 Precio desde: ${vuelo.precioBase} €", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun VueloCombinadoCard(ida: Vuelo, vuelta: Vuelo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✈️ Vuelo de ida", style = MaterialTheme.typography.titleMedium)
            Text("${ida.origen} → ${ida.destino}", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${ida.fechaSalida.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${ida.fechaLlegada.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Duración: ${ida.duracion}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Text("🔁 Vuelo de vuelta", style = MaterialTheme.typography.titleMedium)
            Text("${vuelta.origen} → ${vuelta.destino}", style = MaterialTheme.typography.bodyLarge)
            Text("Salida: ${vuelta.fechaSalida.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Llegada: ${vuelta.fechaLlegada.toDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Duración: ${vuelta.duracion}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            val precioTotal = ida.precioBase + vuelta.precioBase
            Text("💰 Precio total: $precioTotal €", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadosScreenSimplePreview() {
    val vueloIda = Vuelo(
        id = "1",
        origen = "Madrid",
        destino = "Chongqing",
        fechaSalida = Timestamp.now(),
        fechaLlegada = Timestamp.now(),
        duracion = "11h 15min",
        temporada = "alta",
        precioBase = 320,
        asientosTurista = 50,
        asientosPremium = 20,
        asientosBusiness = 10
    )

    val vueloVuelta = Vuelo(
        id = "2",
        origen = "Chongqing",
        destino = "Madrid",
        fechaSalida = Timestamp.now(),
        fechaLlegada = Timestamp.now(),
        duracion = "12h 00min",
        temporada = "alta",
        precioBase = 340,
        asientosTurista = 45,
        asientosPremium = 18,
        asientosBusiness = 8
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("🛫 Ejemplo vuelo solo ida", style = MaterialTheme.typography.titleMedium)
        VueloCard(vuelo = vueloIda, onClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        Text("🔁 Ejemplo vuelo ida + vuelta", style = MaterialTheme.typography.titleMedium)
        VueloCombinadoCard(ida = vueloIda, vuelta = vueloVuelta, onClick = {})
    }
}
