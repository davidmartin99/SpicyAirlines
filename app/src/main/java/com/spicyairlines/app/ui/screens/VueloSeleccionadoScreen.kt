package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import com.spicyairlines.app.ui.viewmodel.SharedViewModel

// Pantalla de Resumen del Vuelo Seleccionado
@Composable
fun VueloSeleccionadoScreen(
    sharedViewModel: SharedViewModel,
    onContinuarClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    // Variables de estado obtenidas del SharedViewModel
    val vueloIda by sharedViewModel.vueloSeleccionado.collectAsState()
    val vueloVuelta by sharedViewModel.vueloVueltaSeleccionado.collectAsState()
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val precioPorBillete by sharedViewModel.precioTotal.collectAsState()
    val adultos by sharedViewModel.adultos.collectAsState()
    val ninos by sharedViewModel.ninos.collectAsState()

    // Calcula el precio total de la reserva
    val precioTotal = precioPorBillete * adultos
    sharedViewModel.actualizarPrecioTotalReserva(precioTotal)

    // Variable para manejar mensajes de error
    var error by remember { mutableStateOf<String?>(null) }

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Resumen del vuelo", style = MaterialTheme.typography.titleLarge)
            }

            // Muestra el vuelo seleccionado (ida o ida y vuelta)
            item {
                vueloVuelta?.let { vuelta ->
                    VueloCombinadoCard(
                        ida = vueloIda!!,
                        vuelta = vuelta,
                        sharedViewModel = sharedViewModel,
                        onClick = {}
                    )
                } ?: VueloCard(
                    vuelo = vueloIda!!,
                    sharedViewModel = sharedViewModel,
                    onClick = {}
                )
            }

            item {
                Divider()
            }

            // Detalles del vuelo
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Clase seleccionada: $clase")
                    Text("Adultos: $adultos")
                    Text("Niños: $ninos (gratis)")
                    Text("Precio por billete: $precioPorBillete €")
                    Text("Total a pagar: $precioTotal €", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Mensaje de error si existe
            item {
                error?.let {
                    MensajeErrorConIcono(mensaje = it)
                }
            }

            // Botón para continuar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        error = null
                        onContinuarClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}
