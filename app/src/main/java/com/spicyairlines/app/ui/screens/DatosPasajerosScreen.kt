package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.R
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.DatosPasajerosViewModel
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import com.spicyairlines.app.ui.utils.DatePickerPasajero
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.utils.edadesSonValidas
import com.spicyairlines.app.utils.validarPasajero

@Composable
fun DatosPasajerosScreen(
    sharedViewModel: SharedViewModel,
    onContinuarClick: () -> Unit,
    viewModel: DatosPasajerosViewModel = viewModel(),
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val totalPasajeros = sharedViewModel.totalPasajeros

    LaunchedEffect(Unit) {
        viewModel.inicializarFormularios(totalPasajeros)
    }

    val pasajeros by viewModel.pasajeros.collectAsState()
    val errores by viewModel.errores.collectAsState()

    BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            pasajeros.forEachIndexed { index, pasajero ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)

                    // Nombre
                    OutlinedTextField(
                        value = pasajero.nombre,
                        onValueChange = { viewModel.actualizarCampo(index, "nombre", it) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Nombre") == true) {
                        MensajeErrorConIcono(errores[index] ?: "")
                    }

                    // Apellidos
                    OutlinedTextField(
                        value = pasajero.apellidos,
                        onValueChange = { viewModel.actualizarCampo(index, "apellidos", it) },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Apellidos") == true) {
                        MensajeErrorConIcono(errores[index] ?: "")
                    }

                    // Fecha de nacimiento
                    DatePickerPasajero(
                        label = "Fecha de nacimiento",
                        initialDate = pasajero.fechaNacimiento.toDate(),
                        onDateSelected = { viewModel.actualizarFechaNacimiento(index, it) }
                    )

                    // Número de Pasaporte
                    OutlinedTextField(
                        value = pasajero.numeroPasaporte,
                        onValueChange = { viewModel.actualizarCampo(index, "numeroPasaporte", it) },
                        label = { Text("Número de pasaporte (3 letras + 6 números)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Pasaporte") == true) {
                        MensajeErrorConIcono(errores[index] ?: "")
                    }

                    // Teléfono
                    OutlinedTextField(
                        value = pasajero.telefono,
                        onValueChange = { viewModel.actualizarCampo(index, "telefono", it) },
                        label = { Text("Teléfono (9 dígitos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Teléfono") == true) {
                        MensajeErrorConIcono(errores[index] ?: "")
                    }
                }
                Divider(thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.validarTodosLosPasajeros()
                    if (viewModel.validarTodosLosPasajeros()) {
                        sharedViewModel.establecerPasajeros(pasajeros)
                        onContinuarClick()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar y continuar")
            }
        }
    }
}
