package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.ReservaConVuelo
import com.spicyairlines.app.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onCerrarSesion: () -> Unit,
    onBack: () -> Unit,
    onEditarPerfil: () -> Unit // ✅ nuevo parámetro
) {
    val reservas by viewModel.reservas.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarReservasUsuario()
    }

    BasePantalla(onBack = onBack) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { auth.signOut(); onCerrarSesion() }) {
                    Text("Cerrar sesión")
                }

                Button(onClick = { showDropdown = !showDropdown }) {
                    Text("Tus reservas")
                }

                Button(onClick = { onEditarPerfil() }) {
                    Text("Editar perfil")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showDropdown) {
                LazyColumn {
                    items(reservas.groupBy { it.reserva.fechaReserva.toDate() }.toSortedMap(reverseOrder()).entries.toList()) { entry ->
                        val reserva = entry.value.first()
                        val fechaReserva = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.key)
                        val id = fechaReserva + reserva.reserva.fechaReserva.seconds

                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { expandedMap[id] = !(expandedMap[id] ?: false) }
                            ) {
                                Text("Reserva del $fechaReserva")
                            }

                            if (expandedMap[id] == true) {
                                entry.value.forEach { reservaConVuelo ->
                                    ReservaResumen(reservaConVuelo)
                                }
                            }
                        }
                    }
                }
            } else if (reservas.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("No tienes reservas aún.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

}

@Composable
fun ReservaResumen(reservaConVuelo: ReservaConVuelo) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val vuelo = reservaConVuelo.vuelo
    val reserva = reservaConVuelo.reserva
    val adultos = reserva.adultos
    val menores = reserva.menores
    val totalPasajeros = adultos + menores

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destino: ${vuelo.destino}", style = MaterialTheme.typography.titleMedium)
            Text("Salida: ${formatoFecha.format(vuelo.fechaSalida.toDate())} a las ${formatoHora.format(vuelo.fechaSalida.toDate())}")
            Text("Llegada: ${formatoFecha.format(vuelo.fechaLlegada.toDate())} a las ${formatoHora.format(vuelo.fechaLlegada.toDate())}")
            Text("Clase: ${reserva.clase}")
            Text("Pasajeros: $totalPasajeros ($adultos adulto(s), $menores menor(es))")
            Text("Precio total: ${reserva.precioTotal}€")
        }
    }
}
