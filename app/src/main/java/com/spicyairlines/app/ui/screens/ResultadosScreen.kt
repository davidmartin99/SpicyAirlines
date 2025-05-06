package com.spicyairlines.app.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.R
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
    var expanded by remember { mutableStateOf(false) }

    BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (cargaCompletada && (vuelosIda.isNotEmpty() || combinacionesValidas.isNotEmpty())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (vuelosVuelta.isEmpty()) "Vuelos disponibles" else "Combinaciones de vuelos",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.filter),
                                contentDescription = "Ordenar vuelos"
                            )
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                !cargaCompletada -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                vuelosIda.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_results),
                            contentDescription = "Sin resultados",
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontraron vuelos disponibles.")
                    }
                }

                vuelosVuelta.isEmpty() -> {
                    val vuelosOrdenados = ordenarVuelosIda(vuelosIda, ordenPrecio)
                    LazyColumn {
                        items(vuelosOrdenados) { vuelo ->
                            VueloCard(vuelo = vuelo, sharedViewModel = sharedViewModel) {
                                sharedViewModel.seleccionarVuelo(vuelo)
                                sharedViewModel.seleccionarVueloVuelta(null)
                                sharedViewModel.calcularPrecioBillete(vuelo)
                                onSeleccionarVuelo()
                            }
                        }
                    }
                }

                else -> {
                    val combinacionesOrdenadas = ordenarCombinaciones(combinacionesValidas, ordenPrecio)
                    LazyColumn {
                        items(combinacionesOrdenadas) { (ida, vuelta) ->
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

    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (isPressed) 10.dp else 6.dp)
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFE1F5FE) else colorPorTemporada(vuelo.temporada)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                isPressed = !isPressed
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.vuelo_ida), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(vuelo.origen, style = MaterialTheme.typography.titleMedium)
                Icon(painter = painterResource(id = R.drawable.flecha), contentDescription = null)
                Text(vuelo.destino, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            InfoItem(R.drawable.reloj, "Salida: ${formatearFechaHoraLocal(vuelo.fechaSalida, vuelo.origen)}")
            InfoItem(R.drawable.reloj, "Llegada: ${formatearFechaHoraLocal(vuelo.fechaLlegada, vuelo.destino)}")
            InfoItem(R.drawable.duracion, "Duración: ${vuelo.duracion}")
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            InfoItem(R.drawable.euro, "Precio por billete: $precioPorPasajero €")
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

    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (isPressed) 10.dp else 6.dp)
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFE1F5FE) else colorPorTemporada(ida.temporada)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                isPressed = !isPressed
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.vuelo_ida), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vuelo de ida", style = MaterialTheme.typography.titleMedium)
            }
            SectionVuelo(R.drawable.vuelo_ida, ida)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.vuelo_vuelta), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vuelo de vuelta", style = MaterialTheme.typography.titleMedium)
            }
            SectionVuelo(R.drawable.vuelo_vuelta, vuelta)

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            InfoItem(R.drawable.euro, "Precio por billete: $precioPorPasajero €")
        }
    }
}

@Composable
fun SectionVuelo(iconId: Int, vuelo: Vuelo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(vuelo.origen)
        Icon(painter = painterResource(id = R.drawable.flecha), contentDescription = null)
        Text(vuelo.destino)
    }
    InfoItem(R.drawable.reloj, "Salida: ${formatearFechaHoraLocal(vuelo.fechaSalida, vuelo.origen)}")
    InfoItem(R.drawable.reloj, "Llegada: ${formatearFechaHoraLocal(vuelo.fechaLlegada, vuelo.destino)}")
}

@Composable
fun InfoItem(iconId: Int, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto)
    }
}

fun ordenarVuelosIda(vuelos: List<Vuelo>, orden: String): List<Vuelo> {
    return if (orden == "Mayor a menor") vuelos.sortedByDescending { it.precioBase }
    else vuelos.sortedBy { it.precioBase }
}

fun ordenarCombinaciones(combinaciones: List<Pair<Vuelo, Vuelo>>, orden: String): List<Pair<Vuelo, Vuelo>> {
    return if (orden == "Mayor a menor") combinaciones.sortedByDescending { it.first.precioBase + it.second.precioBase }
    else combinaciones.sortedBy { it.first.precioBase + it.second.precioBase }
}

fun colorPorTemporada(temporada: String): Color {
    return when (temporada) {
        "alta" -> Color(0xFF65010C)
        "media" -> Color(0xFF9A5E02)
        "baja" -> Color(0xFF07490A)
        else -> Color.White
    }
}
