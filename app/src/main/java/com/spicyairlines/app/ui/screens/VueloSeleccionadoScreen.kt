package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.ui.utils.HoraUTC.formatearFechaHoraLocal
import com.spicyairlines.app.ui.viewmodel.SharedViewModel

@Composable
fun VueloSeleccionadoScreen(
    sharedViewModel: SharedViewModel,
    onContinuarClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val vueloIda by sharedViewModel.vueloSeleccionado.collectAsState()
    val vueloVuelta by sharedViewModel.vueloVueltaSeleccionado.collectAsState()
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val precioPorBillete by sharedViewModel.precioTotal.collectAsState()
    val adultos by sharedViewModel.adultos.collectAsState()
    val ninos by sharedViewModel.ninos.collectAsState()

    val precioTotal = precioPorBillete * adultos
    sharedViewModel.actualizarPrecioTotalReserva(precioTotal)

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text("Resumen del vuelo", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            vueloIda?.let {
                Text("🛫 Ida: ${it.origen} → ${it.destino}")
                Text("Salida: ${formatearFechaHoraLocal(it.fechaSalida, it.origen)}")
                Text("Llegada: ${formatearFechaHoraLocal(it.fechaLlegada, it.destino)}")
                Text("Duración: ${it.duracion}")
            }

            vueloVuelta?.let {
                Spacer(Modifier.height(16.dp))
                Text("🔁 Vuelta: ${it.origen} → ${it.destino}")
                Text("Salida: ${formatearFechaHoraLocal(it.fechaSalida, it.origen)}")
                Text("Llegada: ${formatearFechaHoraLocal(it.fechaLlegada, it.destino)}")
                Text("Duración: ${it.duracion}")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Clase seleccionada: $clase")
            Text("Adultos: $adultos")
            Text("Niños: $ninos (gratis)")
            Text("Precio por billete: $precioPorBillete €")
            Text("💰 Total a pagar: $precioTotal €", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onContinuarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}
