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
    viewModel: SeleccionClaseViewModel = viewModel()
) {
    val vuelo = sharedViewModel.vueloSeleccionado ?: return

    val disponibilidad by viewModel.disponibilidad.collectAsState()

    LaunchedEffect(vuelo.id) {
        viewModel.consultarAsientosDisponibles(vuelo.id)
    }

    BasePantalla(title = "Selecciona clase") {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Vuelo a ${vuelo.ciudadDestino} (${vuelo.fechaIda} - ${vuelo.fechaVuelta})")
            Spacer(modifier = Modifier.height(16.dp))

            val precios = mapOf(
                "Turista" to vuelo.precioBase,
                "Premium" to vuelo.precioBase + 120,
                "Business" to vuelo.precioBase + 320
            )

            listOf("Turista", "Premium", "Business").forEach { clase ->
                val asientos = disponibilidad[clase] ?: 0
                val agotado = asientos <= 0
                val texto = if (agotado) "$clase - Agotado" else "$clase - $asientos disponibles - ${precios[clase]}€"

                Button(
                    onClick = {
                        if (!agotado) {
                            sharedViewModel.claseSeleccionada = clase
                            sharedViewModel.precioTotal = (precios[clase] ?: vuelo.precioBase).toDouble()
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
}
