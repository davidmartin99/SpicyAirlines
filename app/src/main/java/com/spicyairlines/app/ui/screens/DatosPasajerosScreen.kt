package com.spicyairlines.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

    var mostrarErrorCamposVacios by rememberSaveable { mutableStateOf(false) }
    var mostrarErrorEdad by rememberSaveable { mutableStateOf(false) }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.id_card),
                            contentDescription = "Pasajero",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)
                    }

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

                    DatePickerPasajero(
                        label = "Fecha de nacimiento",
                        initialDate = pasajero.fechaNacimiento.toDate(),
                        onDateSelected = { viewModel.actualizarFechaNacimiento(index, it) }
                    )

                    OutlinedTextField(
                        value = pasajero.numeroPasaporte,
                        onValueChange = { viewModel.actualizarCampo(index, "numeroPasaporte", it) },
                        label = { Text("Número de pasaporte") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pasajero.telefono,
                        onValueChange = { viewModel.actualizarCampo(index, "telefono", it) },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Divider(thickness = 1.dp)
            }

            if (mostrarErrorCamposVacios) {
                MensajeErrorConIcono("Por favor, rellena todos los campos obligatorios.")
            }

            if (mostrarErrorEdad) {
                MensajeErrorConIcono("Las fechas de nacimiento no coinciden con el número de adultos (≥3 años) y niños (<3 años) seleccionados.")
            }

            Button(
                onClick = {
                    val hayCamposVacios = pasajeros.any {
                        it.nombre.isBlank() || it.apellidos.isBlank() ||
                                it.numeroPasaporte.isBlank() || it.telefono.isBlank()
                    }

                    val edadesCorrectas = edadesSonValidas(
                        pasajeros = pasajeros,
                        adultosEsperados = sharedViewModel.adultos.value,
                        ninosEsperados = sharedViewModel.ninos.value
                    )

                    when {
                        hayCamposVacios -> {
                            mostrarErrorCamposVacios = true
                            mostrarErrorEdad = false
                        }
                        !edadesCorrectas -> {
                            mostrarErrorEdad = true
                            mostrarErrorCamposVacios = false
                        }
                        else -> {
                            mostrarErrorCamposVacios = false
                            mostrarErrorEdad = false
                            sharedViewModel.establecerPasajeros(pasajeros)
                            onContinuarClick()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar y continuar")
            }
        }
    }
}
