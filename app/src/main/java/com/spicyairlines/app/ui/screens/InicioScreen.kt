package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.components.DatePickerFirebase
import com.spicyairlines.app.viewmodel.InicioViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel
import com.google.firebase.Timestamp
import java.util.*

@Composable
fun InicioScreen(
    viewModel: InicioViewModel = viewModel(),
    resultadosViewModel: ResultadosViewModel = viewModel(),
    onBuscarClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    var errorFecha by remember { mutableStateOf(false) }
    var errorPasajeros by remember { mutableStateOf(false) }

    BasePantalla(onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ciudad de origen
            DropdownMenuOrigen(
                selected = viewModel.ciudadOrigen.value,
                onSeleccion = { viewModel.ciudadOrigen.value = it }
            )

            // Ciudad destino
            DropdownMenuDestino(
                selected = viewModel.ciudadDestino.value,
                onSeleccion = { viewModel.ciudadDestino.value = it },
                ciudadOrigen = viewModel.ciudadOrigen.value
            )

            // Fecha de ida
            DatePickerFirebase(
                label = "Fecha de ida",
                initialDate = viewModel.fechaIda.value?.toDate() ?: Date(),
                onDateSelected = {
                    viewModel.fechaIda.value = Timestamp(it)
                    errorFecha = false
                }
            )

            // Fecha de vuelta (opcional)
            DatePickerFirebase(
                label = "Fecha de vuelta (opcional)",
                initialDate = viewModel.fechaVuelta.value?.toDate() ?: Date(),
                onDateSelected = {
                    viewModel.fechaVuelta.value = Timestamp(it)
                    errorFecha = false
                }
            )

            if (errorFecha) {
                Text(
                    text = "La fecha de vuelta no puede ser anterior a la de ida.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (errorPasajeros) {
                Text(
                    text = "Debes seleccionar al menos 1 pasajero.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Adultos
            OutlinedTextField(
                value = viewModel.adultos.value.toString(),
                onValueChange = { viewModel.adultos.value = it.toIntOrNull() ?: 0 },
                label = { Text("Nº de adultos") },
                singleLine = true
            )

            // Niños
            OutlinedTextField(
                value = viewModel.ninos.value.toString(),
                onValueChange = { viewModel.ninos.value = it.toIntOrNull() ?: 0 },
                label = { Text("Nº de niños menores de 3") },
                singleLine = true
            )

            Button(
                onClick = {
                    val ida = viewModel.fechaIda.value
                    val vuelta = viewModel.fechaVuelta.value
                    val totalPasajeros = viewModel.adultos.value + viewModel.ninos.value

                    errorFecha = ida != null && vuelta != null && vuelta < ida
                    errorPasajeros = totalPasajeros == 0

                    if (!errorFecha && !errorPasajeros) {
                        resultadosViewModel.cargarVuelos(
                            destino = viewModel.ciudadDestino.value,
                            fechaIda = ida,
                            fechaVuelta = vuelta
                        )
                        onBuscarClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.fechaIda.value != null && viewModel.ciudadDestino.value.isNotBlank()
            ) {
                Text("Buscar vuelos")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuOrigen(
    selected: String,
    onSeleccion: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf("Madrid", "Chongqing", "Chengdu")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ciudad de origen") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ciudades.forEach { ciudad ->
                DropdownMenuItem(
                    text = { Text(ciudad) },
                    onClick = {
                        onSeleccion(ciudad)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDestino(
    selected: String,
    onSeleccion: (String) -> Unit,
    ciudadOrigen: String // Recibimos la ciudad de origen
) {
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf("Madrid", "Chongqing", "Chengdu").filter { it != ciudadOrigen } // Filtramos la ciudad de origen

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ciudad de destino") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ciudades.forEach { ciudad ->
                DropdownMenuItem(
                    text = { Text(ciudad) },
                    onClick = {
                        onSeleccion(ciudad)
                        expanded = false
                    }
                )
            }
        }
    }
}
