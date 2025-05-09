package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.DatosPasajerosViewModel
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import com.spicyairlines.app.ui.utils.DatePickerPasajero

@Composable
fun DatosPasajerosScreen(
    sharedViewModel: SharedViewModel,
    onContinuarClick: () -> Unit,
    viewModel: DatosPasajerosViewModel = viewModel(),
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val totalPasajeros = sharedViewModel.totalPasajeros
    val adultos by sharedViewModel.adultos.collectAsState()
    val ninos by sharedViewModel.ninos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.inicializarFormularios(totalPasajeros, adultos, ninos)
    }

    val pasajeros by viewModel.pasajeros.collectAsState()
    val errores by viewModel.errores.collectAsState()

    var mostrarErrores by remember { mutableStateOf(false) }

    BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            pasajeros.forEachIndexed { index, pasajero ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = pasajero.nombre,
                        onValueChange = { viewModel.actualizarCampo(index, "nombre", it) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pasajero.apellidos,
                        onValueChange = { viewModel.actualizarCampo(index, "apellidos", it) },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pasajero.telefono,
                        onValueChange = { viewModel.actualizarCampo(index, "telefono", it) },
                        label = { Text("Teléfono (9 dígitos)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pasajero.numeroPasaporte,
                        onValueChange = { viewModel.actualizarCampo(index, "numeroPasaporte", it) },
                        label = { Text("Número de pasaporte (3 letras + 6 números)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DatePickerPasajero(
                        label = "Fecha de nacimiento",
                        initialDate = pasajero.fechaNacimiento.toDate(),
                        onDateSelected = { viewModel.actualizarFechaNacimiento(index, it) }
                    )
                }
                Divider(thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra los errores como resumen al final
            if (mostrarErrores) {
                errores.forEach { error ->
                    error?.let {
                        MensajeErrorConIcono(it)
                    }
                }
            }

            Button(
                onClick = {
                    mostrarErrores = true
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
