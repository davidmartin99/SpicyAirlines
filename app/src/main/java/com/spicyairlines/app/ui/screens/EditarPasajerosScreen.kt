package com.spicyairlines.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.spicyairlines.app.R
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import com.spicyairlines.app.ui.utils.DatePickerPasajero
import com.spicyairlines.app.ui.viewmodel.EditarPasajerosViewModel
import com.spicyairlines.app.utils.validarPasajero
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Pantalla para Editar Pasajeros de una Reserva
@Composable
fun EditarPasajerosScreen(
    navController: NavController,
    reservaId: String,
    viewModel: EditarPasajerosViewModel = viewModel()
) {
    // Variables de estado
    val pasajeros by viewModel.pasajeros.collectAsState()
    val vuelos by viewModel.vuelos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cambiosGuardados by viewModel.cambiosGuardados.collectAsState()

    // Para mostrar mensajes de éxito o error
    val snackbarHostState = remember { SnackbarHostState() }
    val errores = remember { mutableStateListOf<String?>() }

    // Cargar pasajeros y vuelos de la reserva
    LaunchedEffect(Unit) {
        viewModel.cargarPasajeros(reservaId)
        viewModel.cargarVuelosDeReserva(reservaId)
    }

    // Inicializar errores para cada pasajero
    LaunchedEffect(pasajeros) {
        errores.clear()
        errores.addAll(List(pasajeros.size) { null })
    }

    // Mostrar mensaje de éxito al guardar
    LaunchedEffect(cambiosGuardados) {
        if (cambiosGuardados) {
            snackbarHostState.showSnackbar("Cambios guardados correctamente.")
            delay(4000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    BasePantalla(
        onBack = { navController.popBackStack() },
        snackbarHostState = snackbarHostState
    ) { padding ->
        if (isLoading) { // Indicador de carga

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mostrar detalles de los vuelos asociados
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
                                Spacer(modifier = Modifier.height(12.dp))

                                vuelos.forEachIndexed { index, vuelo ->
                                    val iconId = if (index == 0) R.drawable.vuelo_ida else R.drawable.vuelo_vuelta
                                    val titulo = if (index == 0) "Vuelo de ida" else "Vuelo de vuelta"

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = iconId),
                                                contentDescription = titulo,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(titulo, style = MaterialTheme.typography.titleSmall)
                                        }

                                        Text("Ruta: ${vuelo.origen} → ${vuelo.destino}")
                                        Text("Salida: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(vuelo.fechaSalida.toDate())}")
                                        Text("Llegada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(vuelo.fechaLlegada.toDate())}")
                                    }

                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                items(pasajeros.size) { index ->
                    val pasajero = pasajeros[index]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.id_card),
                                contentDescription = "Pasajero",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pasajero ${index + 1}", style = MaterialTheme.typography.titleMedium)
                        }

                        OutlinedTextField(
                            value = pasajero.nombre,
                            onValueChange = { viewModel.actualizarNombre(index, it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errores.getOrNull(index)?.contains("Nombre") == true) {
                            MensajeErrorConIcono("El nombre no puede estar vacío.")
                        }

                        OutlinedTextField(
                            value = pasajero.apellidos,
                            onValueChange = { viewModel.actualizarApellidos(index, it) },
                            label = { Text("Apellidos") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errores.getOrNull(index)?.contains("Apellidos") == true) {
                            MensajeErrorConIcono("Los apellidos no pueden estar vacíos.")
                        }

                        OutlinedTextField(
                            value = pasajero.numeroPasaporte,
                            onValueChange = { viewModel.actualizarPasaporte(index, it) },
                            label = { Text("Número de pasaporte") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errores.getOrNull(index)?.contains("pasaporte") == true) {
                            MensajeErrorConIcono("Número de pasaporte inválido (3 letras + 6 números + 1 letra/dígito).")
                        }

                        OutlinedTextField(
                            value = pasajero.telefono,
                            onValueChange = { viewModel.actualizarTelefono(index, it) },
                            label = { Text("Teléfono (9 dígitos)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errores.getOrNull(index)?.contains("teléfono") == true) {
                            MensajeErrorConIcono("El teléfono debe tener exactamente 9 dígitos numéricos.")
                        }

                        DatePickerPasajero(
                            label = "Fecha de nacimiento",
                            initialDate = pasajero.fechaNacimiento.toDate(),
                            onDateSelected = { viewModel.actualizarFechaNacimiento(index, it) }
                        )

                        HorizontalDivider(thickness = 1.dp)
                    }
                }

                // Botón para guardar cambios
                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
