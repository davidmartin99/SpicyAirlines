package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.spicyairlines.app.R
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.ReservaConVuelo
import com.spicyairlines.app.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.*

// Pantalla de Perfil del Usuario
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel(),
    onCerrarSesion: () -> Unit,
    onBack: () -> Unit,
    onEditarPerfil: () -> Unit
) {
    // Variables de estado
    val reservas by viewModel.reservas.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }
    var showDropdown by remember { mutableStateOf(false) }

    // Cargar las reservas del usuario al iniciar
    LaunchedEffect(Unit) {
        viewModel.cargarReservasUsuario()
    }

    BasePantalla(
        onBack = onBack
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Botones de acción
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { auth.signOut(); onCerrarSesion() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }

                Button(
                    onClick = { onEditarPerfil() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar perfil")
                }

                Button(
                    onClick = { showDropdown = !showDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reservas")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Listado de reservas
            if (showDropdown) {
                if (reservas.isEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    // Mensaje si no hay reservas
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.no_results),
                            contentDescription = "Sin reservas",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "No tienes reservas aún.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn {
                        items(
                            reservas
                                .groupBy { it.reserva.fechaReserva.toDate() }
                                .toSortedMap(reverseOrder())
                                .entries.toList()
                        ) { entry ->
                            val reservaGroup = entry.value
                            val reservaId = reservaGroup.first().reserva.id
                            val fechaReserva = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.key)

                            Column(modifier = Modifier.fillMaxWidth()) {
                                TextButton(
                                    onClick = {
                                        expandedMap[reservaId] = !(expandedMap[reservaId] ?: false)
                                    }
                                ) {
                                    Text("Reserva del $fechaReserva")
                                }

                                if (expandedMap[reservaId] == true) {
                                    ReservaResumen(
                                        reservasConVuelos = reservaGroup,
                                        onEditarPasajeros = {
                                            navController.navigate("editarPasajeros/$reservaId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable para mostrar el resumen de una reserva
@Composable
fun ReservaResumen(
    reservasConVuelos: List<ReservaConVuelo>,
    onEditarPasajeros: () -> Unit
) {
    // Formatos para mostrar fecha y hora
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

    val reserva = reservasConVuelos.first().reserva
    val adultos = reserva.adultos
    val menores = reserva.menores
    val totalPasajeros = adultos + menores

    // Tarjeta (Card) para mostrar el resumen
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Muestra los detalles de cada vuelo (ida y vuelta)
            reservasConVuelos.forEachIndexed { index, reservaConVuelo ->
                val vuelo = reservaConVuelo.vuelo
                Text(
                    text = if (index == 0) "Vuelo de ida" else "Vuelo de vuelta",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Origen: ${vuelo.origen}")
                Text("Destino: ${vuelo.destino}")
                Text("Salida: ${formatoFecha.format(vuelo.fechaSalida.toDate())} a las ${formatoHora.format(vuelo.fechaSalida.toDate())}")
                Text("Llegada: ${formatoFecha.format(vuelo.fechaLlegada.toDate())} a las ${formatoHora.format(vuelo.fechaLlegada.toDate())}")
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Información general de la reserva
            Text("Clase: ${reserva.clase}")
            Text("Pasajeros: $totalPasajeros ($adultos adulto(s), $menores menor(es))")
            Text("Precio total: ${reserva.precioTotal}€")
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onEditarPasajeros,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar pasajeros")
            }
        }
    }
}
