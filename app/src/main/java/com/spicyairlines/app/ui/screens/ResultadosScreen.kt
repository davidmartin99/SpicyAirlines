package com.spicyairlines.app.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.Timestamp

// Pantalla de Resultados de Vuelos
@Composable
fun ResultadosScreen(
    sharedViewModel: SharedViewModel,
    resultadosViewModel: ResultadosViewModel,
    onSeleccionarVuelo: () -> Unit,
    onBack: () -> Unit,
    onPerfilClick: () -> Unit
) {
    // Variables de estado
    val vuelosIda by resultadosViewModel.vuelosIda.collectAsState()
    val vuelosVuelta by resultadosViewModel.vuelosVuelta.collectAsState()
    val combinacionesValidas by resultadosViewModel.combinacionesValidas.collectAsState()
    val cargaCompletada by resultadosViewModel.cargaCompletada.collectAsState()

    // Estado para ordenar vuelos
    // Estado para ordenar vuelos
    var ordenPrecio by remember { mutableStateOf("Precio (menor a mayor)") }
    val opcionesOrden = listOf(
        Pair("Temporada (baja → alta)", R.drawable.flight),
        Pair("Precio (menor a mayor)", R.drawable.euro),
        Pair("Precio (mayor a menor)", R.drawable.euro)
    )
    var expanded by remember { mutableStateOf(false) }


    // Estructura de la pantalla base
    BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Filtro de orden
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

                    // Menú desplegable para ordenar
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
                            opcionesOrden.forEach { (texto, icono) ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = icono),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(texto)
                                        }
                                    },
                                    onClick = {
                                        ordenPrecio = texto
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Muestra los vuelos según el estado de carga
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

// Función para mostrar vuelos de ida
@Composable
fun VueloCard(
    vuelo: Vuelo,
    sharedViewModel: SharedViewModel,
    onClick: () -> Unit
) {
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val multiplicador = when (clase) {
        "Premium" -> 1.5
        "Business" -> 2.0
        else -> 1.0
    }
    val precioPorPasajero = vuelo.precioBase * multiplicador

    val fechaIda = formatearFechaHoraLocal(vuelo.fechaSalida, vuelo.origen).split(" ")[0]

    val backgroundColor = colorPorTemporada(vuelo.temporada)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 2.dp, color = Color(0x23E5200E), shape = RoundedCornerShape(20.dp)) // 🟨 Borde amarillo
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Fecha",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(fechaIda)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vuelo solo ida
            Icon(painter = painterResource(id = R.drawable.vuelo_ida), contentDescription = "Vuelo ida")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CiudadYHora(vuelo.origen, vuelo.fechaSalida)
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(id = R.drawable.flecha), contentDescription = null)
                Spacer(modifier = Modifier.weight(1f))
                CiudadYHora(vuelo.destino, vuelo.fechaLlegada)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.duracion), contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(vuelo.duracion, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.White.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(painter = painterResource(id = R.drawable.euro), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Total: ${precioPorPasajero.toInt()} €/p", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


// Función para mostrar combinaciones de vuelos ida y vuelta
@Composable
fun VueloCombinadoCard(
    ida: Vuelo,
    vuelta: Vuelo,
    sharedViewModel: SharedViewModel,
    onClick: () -> Unit
) {
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val multiplicador = when (clase) {
        "Premium" -> 1.5
        "Business" -> 2.0
        else -> 1.0
    }
    val precioPorPasajero = (ida.precioBase + vuelta.precioBase) * multiplicador

    val fechaIda = formatearFechaHoraLocal(ida.fechaSalida, ida.origen).split(" ")[0]
    val fechaVuelta = formatearFechaHoraLocal(vuelta.fechaLlegada, vuelta.destino).split(" ")[0]

    val backgroundColor = colorPorTemporada(ida.temporada)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 2.dp, color = Color(0x23E5200E), shape = RoundedCornerShape(20.dp)) // 🟨 Borde amarillo
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fecha del viaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Fecha",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("$fechaIda - $fechaVuelta")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // VUELO IDA
            Icon(painter = painterResource(id = R.drawable.vuelo_ida), contentDescription = "Vuelo ida")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CiudadYHora(ida.origen, ida.fechaSalida)
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(id = R.drawable.flecha), contentDescription = null)
                Spacer(modifier = Modifier.weight(1f))
                CiudadYHora(ida.destino, ida.fechaLlegada)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.duracion), contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(ida.duracion, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // VUELO VUELTA
            Icon(painter = painterResource(id = R.drawable.vuelo_vuelta), contentDescription = "Vuelo vuelta")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CiudadYHora(vuelta.origen, vuelta.fechaSalida)
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(id = R.drawable.flecha), contentDescription = null)
                Spacer(modifier = Modifier.weight(1f))
                CiudadYHora(vuelta.destino, vuelta.fechaLlegada)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.duracion), contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(vuelta.duracion, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.White.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Precio abajo a la derecha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(painter = painterResource(id = R.drawable.euro), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Total: ${precioPorPasajero.toInt()} €/p", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Composable para mostrar la ciudad con su hora
@Composable
fun CiudadYHora(ciudad: String, fecha: Timestamp) {
    val partes = formatearFechaHoraLocal(fecha, ciudad).split(" ")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(ciudad)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(painter = painterResource(id = R.drawable.reloj), contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(partes[1])
    }
}

// Función para ordenar vuelos de ida según el precio / temproada
fun ordenarVuelosIda(vuelos: List<Vuelo>, orden: String): List<Vuelo> {
    return when (orden) {
        "Precio (mayor a menor)" -> vuelos.sortedByDescending { it.precioBase }
        "Precio (menor a mayor)" -> vuelos.sortedBy { it.precioBase }
        "Temporada (baja → alta)" -> vuelos.sortedBy {
            when (it.temporada.lowercase()) {
                "baja" -> 0
                "media" -> 1
                "alta" -> 2
                else -> 3
            }
        }
        else -> vuelos
    }
}

// Función para ordenar combinaciones de vuelos (ida y vuelta) por precio total / temporada
fun ordenarCombinaciones(combinaciones: List<Pair<Vuelo, Vuelo>>, orden: String): List<Pair<Vuelo, Vuelo>> {
    return when (orden) {
        "Precio (mayor a menor)" -> combinaciones.sortedByDescending { it.first.precioBase + it.second.precioBase }
        "Precio (menor a mayor)" -> combinaciones.sortedBy { it.first.precioBase + it.second.precioBase }
        "Temporada (baja → alta)" -> combinaciones.sortedBy {
            when (it.first.temporada.lowercase()) {
                "baja" -> 0
                "media" -> 1
                "alta" -> 2
                else -> 3
            }
        }
        else -> combinaciones
    }
}

// Función para asignar color según la temporada del vuelo
fun colorPorTemporada(temporada: String): Color {
    return when (temporada) {
        "alta" -> Color(0xCC7A1004)
        "media" -> Color(0xFF7C310A)
        "baja" -> Color(0xFF9F5B16)
        else -> Color.White
    }
}
