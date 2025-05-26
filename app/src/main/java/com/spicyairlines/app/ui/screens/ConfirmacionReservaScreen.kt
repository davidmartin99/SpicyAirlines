package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ConfirmacionReservaViewModel

// Pantalla de Confirmación de Reserva
@Composable
fun ConfirmacionReservaScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ConfirmacionReservaViewModel = viewModel(),
    onConfirmarClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    // Variables de estado compartidas del SharedViewModel
    val vueloIda by sharedViewModel.vueloSeleccionado.collectAsState()
    val vueloVuelta by sharedViewModel.vueloVueltaSeleccionado.collectAsState()
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val pasajeros by sharedViewModel.pasajeros.collectAsState()
    val total by sharedViewModel.precioTotal.collectAsState()

    // Estados locales para control de carga y errores
    var cargando by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    // Estructura de la pantalla usando BasePantalla
    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar vuelo (ida o ida y vuelta)
            item {
                Text("Resumen de la reserva", style = MaterialTheme.typography.titleLarge)
            }

            item {
                vueloVuelta?.let { vuelta ->
                    VueloCombinadoCard(
                        ida = vueloIda!!,
                        vuelta = vuelta,
                        sharedViewModel = sharedViewModel,
                        onClick = {}
                    )
                } ?: VueloCard(
                    vuelo = vueloIda!!,
                    sharedViewModel = sharedViewModel,
                    onClick = {}
                )
            }

            // Mostrar clase y precio total
            item {
                Text("Clase: $clase")
                Text("Total a pagar: $total €")
            }

            item {
                Text("Pasajeros", style = MaterialTheme.typography.titleMedium)
            }

            // Listar pasajeros
            items(pasajeros) { pasajero: Pasajero ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.id_card),
                            contentDescription = "Pasajero",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${pasajero.nombre} ${pasajero.apellidos}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Column(modifier = Modifier.padding(start = 32.dp, top = 4.dp)) {
                        Text("Pasaporte: ${pasajero.numeroPasaporte}")
                        Text("Teléfono: ${pasajero.telefono}")
                    }

                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
                }
            }

            // Botón de Confirmar (Pagar)
            item {
                if (cargando) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick = {
                            cargando = true
                            viewModel.guardarReservaFirebase(
                                vueloIda = vueloIda!!,
                                vueloVuelta = vueloVuelta,
                                clase = clase,
                                pasajeros = pasajeros,
                                precioTotal = total,
                                onSuccess = {
                                    cargando = false
                                    onConfirmarClick()
                                },
                                onFailure = {
                                    cargando = false
                                    error = "Error: ${it.message}"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pagar")
                    }
                }
            }

            // Mostrar mensaje de error (si existe)
            item {
                error?.let {
                    MensajeErrorConIcono(mensaje = it)
                }
            }
        }
    }
}
