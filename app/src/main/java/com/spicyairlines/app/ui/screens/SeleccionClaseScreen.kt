package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.SeleccionClaseViewModel
import com.spicyairlines.app.ui.viewmodel.SharedViewModel

@Composable
fun SeleccionClaseScreen(
    sharedViewModel: SharedViewModel,
    onContinuarClick: () -> Unit,
    viewModel: SeleccionClaseViewModel = viewModel(),
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val vuelo by sharedViewModel.vueloSeleccionado.collectAsState()
    val disponibilidad by viewModel.disponibilidad.collectAsState()

    vuelo?.let { vueloSeleccionado ->

        LaunchedEffect(vueloSeleccionado.id) {
            viewModel.consultarAsientosDisponibles(vueloSeleccionado.id)
        }

        BasePantalla(
            onBack = onBack,
            onPerfilClick = onPerfilClick
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Vuelo a ${vueloSeleccionado.destino} (${vueloSeleccionado.fechaSalida.toDate()} - ${vueloSeleccionado.fechaLlegada.toDate()})")
                Spacer(modifier = Modifier.height(16.dp))

                val precios = mapOf(
                    "Turista" to vueloSeleccionado.precioBase,
                    "Premium" to vueloSeleccionado.precioBase + 120,
                    "Business" to vueloSeleccionado.precioBase + 320
                )

                listOf("Turista", "Premium", "Business").forEach { clase ->
                    val asientos = disponibilidad[clase] ?: 0
                    val agotado = asientos <= 0
                    val texto = if (agotado) "$clase - Agotado" else "$clase - $asientos disponibles - ${precios[clase]}€"

                    Button(
                        onClick = {
                            if (!agotado) {
                                sharedViewModel.seleccionarClase(clase)
                                sharedViewModel.actualizarPrecioTotal((precios[clase] ?: vueloSeleccionado.precioBase).toDouble())
                                onContinuarClick()
                            }
                        },
                        enabled = !agotado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(texto)
                    }
                }
            }
        }
    } ?: run {
        // Si no hay vuelo, puedes mostrar algo o navegar atrás
        Text("No hay vuelo seleccionado.")
    }
}
