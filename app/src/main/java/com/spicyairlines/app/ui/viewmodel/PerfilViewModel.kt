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

class PerfilViewModel : ViewModel() {

    private val _reservas = MutableStateFlow<List<ReservaConVuelo>>(emptyList())
    val reservas: StateFlow<List<ReservaConVuelo>> = _reservas

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun cargarReservasUsuario() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            db.collection("reservas")
                .whereEqualTo("uidUsuario", uid)
                .get()
                .addOnSuccessListener { reservaDocs ->
                    val reservasTemp = mutableListOf<ReservaConVuelo>()

                    for (reservaDoc in reservaDocs) {
                        val reserva = reservaDoc.toObject(Reserva::class.java)
                        val vueloId = reserva.vueloId

                        db.collection("vuelos")
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
                                    _reservas.value = reservasTemp.sortedByDescending { it.reserva.fechaReserva }
                                }
                            }
                    }
                }
        }
    }
}
