package com.spicyairlines.app.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.components.DatePickerFirebase
import com.spicyairlines.app.viewmodel.InicioViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel
import com.google.firebase.Timestamp
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import java.util.*

@Composable
fun InicioScreen(
    viewModel: InicioViewModel = viewModel(),
    resultadosViewModel: ResultadosViewModel = viewModel(),
    sharedViewModel: SharedViewModel = viewModel(),
    onBuscarClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    var errorFecha by rememberSaveable { mutableStateOf(false) }
    var errorPasajeros by rememberSaveable { mutableStateOf(false) }

    BasePantalla(onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropdownMenuOrigen(
                selected = viewModel.ciudadOrigen.value,
                onSeleccion = { viewModel.ciudadOrigen.value = it }
            )

            DropdownMenuDestino(
                selected = viewModel.ciudadDestino.value,
                onSeleccion = { viewModel.ciudadDestino.value = it },
                ciudadOrigen = viewModel.ciudadOrigen.value
            )

            DatePickerFirebase(
                soloIda = viewModel.soloIda.value,
                fechaIda = viewModel.fechaIda.value,
                fechaVuelta = viewModel.fechaVuelta.value
            ) { ida, vuelta ->
                viewModel.fechaIda.value = ida
                viewModel.fechaVuelta.value = vuelta
                errorFecha = false
            }

            CheckboxSoloIda(viewModel)
            SelectorViajerosClase(viewModel.adultos, viewModel.ninos, viewModel.clase)
            MostrarErrores(errorFecha, errorPasajeros)

            Button(
                onClick = {
                    val ida = viewModel.fechaIda.value
                    val vuelta = if (viewModel.soloIda.value) null else viewModel.fechaVuelta.value
                    val adultos = viewModel.adultos.value
                    val ninos = viewModel.ninos.value
                    val totalPasajeros = adultos + ninos
                    val claseSeleccionada = viewModel.clase.value

                    errorFecha = ida != null && vuelta != null && vuelta < ida
                    errorPasajeros = totalPasajeros == 0

                    if (!errorFecha && !errorPasajeros && ida != null) {
                        resultadosViewModel.cargarVuelos(
                            origen = viewModel.ciudadOrigen.value,
                            destino = viewModel.ciudadDestino.value,
                            fechaIda = ida,
                            fechaVuelta = vuelta,
                            claseSeleccionada = claseSeleccionada,
                            totalPasajeros = totalPasajeros
                        )

                        sharedViewModel.seleccionarClase(claseSeleccionada)
                        sharedViewModel.establecerAdultos(adultos)
                        sharedViewModel.establecerNinos(ninos)

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

@Composable
fun CheckboxSoloIda(viewModel: InicioViewModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = viewModel.soloIda.value,
            onCheckedChange = { viewModel.soloIda.value = it }
        )
        Text("Solo ida")
    }
}

@Composable
fun MostrarErrores(errorFecha: Boolean, errorPasajeros: Boolean) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorViajerosClase(
    adultos: MutableState<Int>,
    ninos: MutableState<Int>,
    clase: MutableState<String>
) {
    var expandedClase by remember { mutableStateOf(false) }
    val clases = listOf("Turista", "Premium", "Business")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Clase")
        ExposedDropdownMenuBox(expanded = expandedClase, onExpandedChange = { expandedClase = !expandedClase }) {
            TextField(
                value = clase.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Clase") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedClase) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedClase,
                onDismissRequest = { expandedClase = false }
            ) {
                clases.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        clase.value = it
                        expandedClase = false
                    })
                }
            }
        }

        SelectorContador("Adultos", adultos, minimo = 1)
        SelectorContador("Niños", ninos, minimo = 0)
    }
}

@Composable
fun SelectorContador(label: String, contador: MutableState<Int>, minimo: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, modifier = Modifier.weight(1f))
        IconButton(onClick = { if (contador.value > minimo) contador.value-- }) { Text("-") }
        Text(contador.value.toString())
        IconButton(onClick = { contador.value++ }) { Text("+") }
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
    ciudadOrigen: String
) {
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf("Madrid", "Chongqing", "Chengdu").filter { it != ciudadOrigen }

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
