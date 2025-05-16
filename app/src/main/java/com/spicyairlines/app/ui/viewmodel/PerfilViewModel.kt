package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.ReservaConVuelo
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para gestionar las reservas del perfil del usuario
class PerfilViewModel : ViewModel() {

    // Flow que contiene las reservas del usuario (con información de los vuelos)
    private val _reservas = MutableStateFlow<List<ReservaConVuelo>>(emptyList())
    val reservas: StateFlow<List<ReservaConVuelo>> = _reservas

    // Instancia de Firebase Firestore y FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Función para cargar las reservas del usuario autenticado
    fun cargarReservasUsuario() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            db.collection("reservas")
                .whereEqualTo("idUsuario", uid)
                .get()
                .addOnSuccessListener { reservaDocs ->
                    val reservasTemp = mutableListOf<ReservaConVuelo>()

                    for (reservaDoc in reservaDocs) {
                        val reserva = reservaDoc.toObject(Reserva::class.java)
                            .copy(id = reservaDoc.id)

                        val vuelosIds = reserva.vuelos

                        // Recorremos todos los vuelos asociados (ida y vuelta si existen)
                        vuelosIds.forEach { vueloId ->
                            cargarVueloYAgregar(reserva, vueloId, reservasTemp)
                        }
                    }
                }
        }
    }

    // Función para cargar un vuelo y agregarlo a la lista de reservas
    private fun cargarVueloYAgregar(
        reserva: Reserva,
        vueloId: String,
        reservasTemp: MutableList<ReservaConVuelo>
    ) {
        db.collection("Vuelos")
            .document(vueloId)
            .get()
            .addOnSuccessListener { vueloDoc ->
                val vuelo = vueloDoc.toObject(Vuelo::class.java)
                if (vuelo != null) {
                    reservasTemp.add(
                        ReservaConVuelo(
                            reserva = reserva,
                            vuelo = vuelo
                        )
                    )
                    // Actualiza la lista de reservas en orden descendente por fecha
                    _reservas.value = reservasTemp.sortedByDescending { it.reserva.fechaReserva }
                }
            }
    }

}
