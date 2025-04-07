package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.components.DatePickerFirebase
import com.spicyairlines.app.viewmodel.InicioViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel

@Composable
fun InicioScreen(
    viewModel: InicioViewModel = viewModel(),
    resultadosViewModel: ResultadosViewModel = viewModel(),
    onBuscarClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    BasePantalla(onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ciudad destino
            DropdownMenuDestino(
                selected = viewModel.ciudadDestino.value,
                onSeleccion = { viewModel.ciudadDestino.value = it }
            )

            // Fecha ida
            DatePickerFirebase(
                label = "Fecha de ida",
                initialDate = viewModel.fechaIda.value,
                onDateSelected = { viewModel.fechaIda.value = it }
            )

            // Fecha vuelta (opcional)
            DatePickerFirebase(
                label = "Fecha de vuelta (opcional)",
                initialDate = viewModel.fechaVuelta.value,
                onDateSelected = { viewModel.fechaVuelta.value = it }
            )

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
                    resultadosViewModel.cargarVuelos(
                        destino = viewModel.ciudadDestino.value,
                        fechaIda = viewModel.fechaIda.value,
                        fechaVuelta = viewModel.fechaVuelta.value
                    )
                    onBuscarClick()
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
fun DropdownMenuDestino(
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
