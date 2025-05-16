package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.components.DatePickerFirebase
import com.spicyairlines.app.viewmodel.PrincipalViewModel
import com.spicyairlines.app.viewmodel.ResultadosViewModel
import com.spicyairlines.app.ui.viewmodel.SharedViewModel

// Pantalla Principal para Buscar Vuelos
@Composable
fun PrincipalScreen(
    viewModel: PrincipalViewModel = viewModel(),
    resultadosViewModel: ResultadosViewModel = viewModel(),
    sharedViewModel: SharedViewModel = viewModel(),
    onBuscarClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    // Estados de error
    var errorFecha by rememberSaveable { mutableStateOf(false) }
    var errorPasajeros by rememberSaveable { mutableStateOf(false) }

    BasePantalla(onPerfilClick = onPerfilClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titulo
            Text(
                text = "Encuentra tu vuelo ideal",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selección de Origen y Destino
            DropdownMenuOrigen(
                selected = viewModel.ciudadOrigen.value,
                onSeleccion = { viewModel.ciudadOrigen.value = it }
            )

            DropdownMenuDestino(
                selected = viewModel.ciudadDestino.value,
                onSeleccion = { viewModel.ciudadDestino.value = it },
                ciudadOrigen = viewModel.ciudadOrigen.value
            )

            // Selector de Fecha
            DatePickerFirebase(
                soloIda = viewModel.soloIda.value,
                fechaIda = viewModel.fechaIda.value,
                fechaVuelta = viewModel.fechaVuelta.value
            ) { ida, vuelta ->
                viewModel.fechaIda.value = ida
                viewModel.fechaVuelta.value = vuelta
                errorFecha = false
            }

            // Checkbox para "Solo ida"
            CheckboxSoloIda(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Viajeros y Clase
            SelectorViajerosClase(viewModel.adultos, viewModel.ninos, viewModel.clase)

            Spacer(modifier = Modifier.height(16.dp))

            MostrarErrores(errorFecha, errorPasajeros)

            // Botón para Buscar
            Button(
                onClick = {
                    val ida = viewModel.fechaIda.value
                    val vuelta = if (viewModel.soloIda.value) null else viewModel.fechaVuelta.value
                    val adultos = viewModel.adultos.value
                    val ninos = viewModel.ninos.value
                    val totalPasajeros = adultos + ninos
                    val claseSeleccionada = viewModel.clase.value

                    // Validar errores
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

                        // Guardar datos seleccionados en el sharedViewModel
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

// Checkbox para seleccionar si el vuelo es "Solo ida"
@Composable
fun CheckboxSoloIda(viewModel: PrincipalViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = viewModel.soloIda.value,
            onCheckedChange = { viewModel.soloIda.value = it }
        )
        Text("Solo ida")
    }
}

// Composable para mostrar errores relacionados con fechas y pasajeros
@Composable
fun MostrarErrores(errorFecha: Boolean, errorPasajeros: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (errorFecha) {
            Text(
                text = "La fecha de vuelta no puede ser anterior a la de ida.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (errorPasajeros) {
            Text(
                text = "Debes seleccionar al menos 1 pasajero.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Selector de viajeros (adultos y niños) y clase de vuelo
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
        Text("Clase", fontWeight = FontWeight.Medium)
        // Menú desplegable para seleccionar clase
        ExposedDropdownMenuBox(expanded = expandedClase, onExpandedChange = { expandedClase = !expandedClase }) {
            TextField(
                value = clase.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona clase") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedClase) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        // Contadores de adultos y niños
        SelectorContador("Adultos ", adultos, minimo = 1)
        SelectorContador("Niños (menores de 3 años)", ninos, minimo = 0)
    }
}

// Contador para seleccionar número de pasajeros
@Composable
fun SelectorContador(label: String, contador: MutableState<Int>, minimo: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, modifier = Modifier.weight(1f))
        IconButton(onClick = { if (contador.value > minimo) contador.value-- }) {
            Text("-")
        }
        Text(contador.value.toString())
        IconButton(onClick = { contador.value++ }) {
            Text("+")
        }
    }
}

// Menú desplegable para seleccionar ciudad de origen
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
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

// Menú desplegable para seleccionar ciudad de destino
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
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
