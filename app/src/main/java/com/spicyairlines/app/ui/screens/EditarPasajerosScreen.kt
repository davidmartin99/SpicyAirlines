package com.spicyairlines.app.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.viewmodel.EditarPasajerosViewModel
import com.spicyairlines.app.utils.validarPasajero
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditarPasajerosScreen(
    navController: NavController,
    reservaId: String,
    viewModel: EditarPasajerosViewModel = viewModel()
) {
    val pasajeros by viewModel.pasajeros.collectAsState()
    val vuelos by viewModel.vuelos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cambiosGuardados by viewModel.cambiosGuardados.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val errores = remember { mutableStateListOf<String?>() }

    // 🚀 Cargar pasajeros y vuelos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarPasajeros(reservaId)
        viewModel.cargarVuelosDeReserva(reservaId)
    }

    // Inicializar lista de errores
    LaunchedEffect(pasajeros) {
        errores.clear()
        errores.addAll(List(pasajeros.size) { null })
    }

    // Mostrar Snackbar si se guardan cambios
    LaunchedEffect(cambiosGuardados) {
        if (cambiosGuardados) {
            snackbarHostState.showSnackbar("✅ Cambios guardados")
            delay(4000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    BasePantalla(
        onBack = { navController.popBackStack() },
        snackbarHostState = snackbarHostState
    ) { padding ->

        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Dentro de BasePantalla
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mostrar vuelos asociados
                if (vuelos.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Vuelos asociados", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                vuelos.forEachIndexed { index, vuelo ->
                                    Text("Vuelo ${index + 1}: ${vuelo.origen} → ${vuelo.destino}")
                                    Text("Salida: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(vuelo.fechaSalida.toDate())}")
                                    Text("Llegada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(vuelo.fechaLlegada.toDate())}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                // Mostrar formulario de pasajeros
                items(pasajeros.size) { index ->
                    val pasajero = pasajeros[index]

                    Text(
                        text = "Pasajero ${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = pasajero.nombre,
                        onValueChange = { viewModel.actualizarNombre(index, it) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Nombre") == true) {
                        Text("El nombre no puede estar vacío.", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pasajero.apellidos,
                        onValueChange = { viewModel.actualizarApellidos(index, it) },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("Apellidos") == true) {
                        Text("Los apellidos no pueden estar vacíos.", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pasajero.numeroPasaporte,
                        onValueChange = { viewModel.actualizarPasaporte(index, it) },
                        label = { Text("Número de pasaporte") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("pasaporte") == true) {
                        Text("Número de pasaporte inválido (3 letras + 6 números + 1 letra/dígito).", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pasajero.telefono,
                        onValueChange = { viewModel.actualizarTelefono(index, it) },
                        label = { Text("Teléfono (6 dígitos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errores.getOrNull(index)?.contains("teléfono") == true) {
                        Text("El teléfono debe tener exactamente 6 dígitos numéricos.", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaTexto = dateFormatter.format(pasajero.fechaNacimiento.toDate())

                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = pasajero.fechaNacimiento.toDate()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedDate = Calendar.getInstance()
                                    selectedDate.set(year, month, dayOfMonth)
                                    viewModel.actualizarFechaNacimiento(index, selectedDate.time)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fecha de nacimiento: $fechaTexto")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(thickness = 1.dp)
                }

                // Botón Guardar cambios
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            var hayErrores = false
                            pasajeros.forEachIndexed { index, pasajero ->
                                val resultado = validarPasajero(pasajero)
                                if (!resultado.esValido) {
                                    errores[index] = resultado.mensajeError
                                    hayErrores = true
                                } else {
                                    errores[index] = null
                                }
                            }

                            if (!hayErrores) {
                                viewModel.guardarCambios(reservaId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }
                }
            }

        }
    }
}
