package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.PasajerosViewModel

@Composable
fun PasajerosScreen(
    viewModel: PasajerosViewModel = viewModel(),
    onContinuarClick: () -> Unit
) {
    BasePantalla() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(viewModel.numPasajeros) { index ->
                val pasajero = viewModel.pasajeros.getOrNull(index) ?: return@repeat

                Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = pasajero.nombre,
                    onValueChange = { viewModel.actualizarPasajero(index, it, "nombre") },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pasajero.apellidos,
                    onValueChange = { viewModel.actualizarPasajero(index, it, "apellidos") },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pasajero.fechaNacimiento,
                    onValueChange = { viewModel.actualizarPasajero(index, it, "fechaNacimiento") },
                    label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pasajero.numeroPasaporte,
                    onValueChange = { viewModel.actualizarPasajero(index, it, "numeroPasaporte") },
                    label = { Text("Número de pasaporte") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pasajero.telefono,
                    onValueChange = { viewModel.actualizarPasajero(index, it, "telefono") },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(thickness = 1.dp)
            }

            Button(
                onClick = onContinuarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}
