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
import com.spicyairlines.app.model.ReservaConVuelo
import com.spicyairlines.app.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onCerrarSesion: () -> Unit
) {
    val reservas by viewModel.reservas.collectAsState()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        viewModel.cargarReservasUsuario()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                auth.signOut()
                onCerrarSesion()
            }) {
                Text("Cerrar sesión")
            }
        }

        Text(
            text = "Tus reservas",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (reservas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes reservas aún.")
            }
        } else {
            LazyColumn {
                items(reservas) { reservaConVuelo ->
                    ReservaItem(reservaConVuelo)
                }
            }
        }
    }
}

@Composable
fun ReservaItem(reservaConVuelo: ReservaConVuelo) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val adultos = reservaConVuelo.reserva.adultos
    val menores = reservaConVuelo.reserva.menores
    val totalPasajeros = adultos + menores

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destino: ${reservaConVuelo.vuelo.ciudadDestino}", style = MaterialTheme.typography.titleMedium)
            Text("Fecha ida: ${reservaConVuelo.vuelo.fechaIda}")
            Text("Fecha vuelta: ${reservaConVuelo.vuelo.fechaVuelta}")
            Text("Clase: ${reservaConVuelo.reserva.clase}")
            Text("Pasajeros: $totalPasajeros ($adultos adulto(s), $menores menor(es))")
            Text("Precio total: ${reservaConVuelo.reserva.precioTotal}€")
            Text("Reservado el: ${formatoFecha.format(reservaConVuelo.reserva.fechaReserva.toDate())}")
        }
    }
}


