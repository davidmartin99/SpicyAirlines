package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    viewModel: InicioViewModel = viewModel(),
    onBuscarClick: () -> Unit
) {
    BasePantalla(title = "SpicyAirlines") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Selecciona destino:")

            DropdownMenuDestino(
                selected = viewModel.ciudadDestino.value,
                onSeleccion = { viewModel.ciudadDestino.value = it }
            )

            OutlinedTextField(
                value = viewModel.fechaIda.value,
                onValueChange = { viewModel.fechaIda.value = it },
                label = { Text("Fecha de ida (YYYY-MM-DD)") },
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.fechaVuelta.value,
                onValueChange = { viewModel.fechaVuelta.value = it },
                label = { Text("Fecha de vuelta (YYYY-MM-DD)") },
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.adultos.value.toString(),
                onValueChange = { viewModel.adultos.value = it.toIntOrNull() ?: 0 },
                label = { Text("Nº de adultos") },
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.ninos.value.toString(),
                onValueChange = { viewModel.ninos.value = it.toIntOrNull() ?: 0 },
                label = { Text("Nº de niños menores de 3") },
                singleLine = true
            )

            Button(
                onClick = onBuscarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar vuelos")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDestino(selected: String, onSeleccion: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf("Chongqing", "Chengdu")

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
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
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
