package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.DatosPasajerosViewModel
import com.spicyairlines.app.model.Pasajero // ✅ Este es el único cambio necesario
import com.spicyairlines.app.ui.utils.DatePickerPasajero
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.utils.edadesSonValidas
import java.util.Calendar

@Composable
fun DatosPasajerosScreen(
    sharedViewModel: SharedViewModel, // ✅ Añade esto
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

    var mostrarErrorCamposVacios by remember { mutableStateOf(false) }
    var mostrarErrorEdad by remember { mutableStateOf(false) }

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            pasajeros.forEachIndexed { index, pasajero ->
                Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

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
                    initialDate = pasajero.fechaNacimiento.toDate(), // ✅ Ya no necesitas el operador `?.`
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

                Spacer(modifier = Modifier.height(16.dp))
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


            if (mostrarErrorCamposVacios) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Por favor, rellena todos los campos obligatorios.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (mostrarErrorEdad) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Las fechas de nacimiento no coinciden con el número de adultos (≥3 años) y niños (<3 años) seleccionados.",
                    color = MaterialTheme.colorScheme.error
                )
            }



        }
    }
}
