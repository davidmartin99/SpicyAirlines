package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.Vuelo
import com.google.firebase.Timestamp

class ConfirmacionReservaViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun guardarReservaFirebase(
        vuelo: Vuelo,
        clase: String,
        pasajeros: List<Pasajero>,
        precioTotal: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        val reserva = Reserva(
            idUsuario = uid,
            vueloId = vuelo.id,
            clase = clase,
            precioTotal = precioTotal,
            fechaReserva = Timestamp.now(),
            estado = true
        )

        db.collection("reservas")
            .add(reserva)
            .addOnSuccessListener { docRef ->
                pasajeros.forEach { pasajero ->
                    db.collection("reservas")
                        .document(docRef.id)
                        .collection("pasajeros")
                        .add(pasajero)
                }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }
}
