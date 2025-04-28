package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.ui.viewmodel.SharedViewModel
import com.spicyairlines.app.viewmodel.ConfirmacionReservaViewModel

@Composable
fun ConfirmacionReservaScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ConfirmacionReservaViewModel = viewModel(),
    onConfirmarClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onBack: () -> Unit
) {
    val vueloIda by sharedViewModel.vueloSeleccionado.collectAsState()
    val vueloVuelta by sharedViewModel.vueloVueltaSeleccionado.collectAsState()
    val clase by sharedViewModel.claseSeleccionada.collectAsState()
    val pasajeros by sharedViewModel.pasajeros.collectAsState()
    val total by sharedViewModel.precioTotal.collectAsState()

    var cargando by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    if (vueloIda == null) {
        BasePantalla(onBack = onBack, onPerfilClick = onPerfilClick) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No se ha seleccionado un vuelo.")
            }
        }
        return
    }

    fun hayAsientosDisponibles(vuelo: Vuelo, clase: String, cantidad: Int): Boolean {
        return when (clase) {
            "Business" -> vuelo.asientosBusiness >= cantidad
            "Premium" -> vuelo.asientosPremium >= cantidad
            else -> vuelo.asientosTurista >= cantidad
        }
    }

    BasePantalla(
        onBack = onBack,
        onPerfilClick = onPerfilClick
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Text("✈️ Resumen de la reserva", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Text("🛫 Ida: ${vueloIda!!.origen} → ${vueloIda!!.destino}")
                Text("   Salida: ${vueloIda!!.fechaSalida.toDate()}")
                Text("   Llegada: ${vueloIda!!.fechaLlegada.toDate()}")
            }

            vueloVuelta?.let {
                item {
                    Text("🔁 Vuelta: ${it.origen} → ${it.destino}")
                    Text("   Salida: ${it.fechaSalida.toDate()}")
                    Text("   Llegada: ${it.fechaLlegada.toDate()}")
                }
            }

            item {
                Text("Clase: $clase")
                Text("Total a pagar: $total €")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text("👥 Pasajeros:", style = MaterialTheme.typography.titleSmall)
            }

            items(pasajeros) { pasajero: Pasajero ->
                Column {
                    Text("🧍 ${pasajero.nombre} ${pasajero.apellidos}")
                    Text("   Nacimiento: ${pasajero.fechaNacimiento.toDate()}")
                    Text("   Pasaporte: ${pasajero.numeroPasaporte}")
                    Text("   Teléfono: ${pasajero.telefono}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                if (cargando) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick = {
                            val totalPasajeros = pasajeros.size

                            val asientosDisponiblesIda = hayAsientosDisponibles(vueloIda!!, clase, totalPasajeros)
                            val asientosDisponiblesVuelta = vueloVuelta?.let {
                                hayAsientosDisponibles(it, clase, totalPasajeros)
                            } ?: true // si no hay vuelo de vuelta, se da por válido

                            if (!asientosDisponiblesIda || !asientosDisponiblesVuelta) {
                                error = "❌ No hay suficientes asientos disponibles en la clase seleccionada."
                                return@Button
                            }

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

            item {
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
