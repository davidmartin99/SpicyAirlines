package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.Vuelo
import com.google.firebase.Timestamp
import java.util.Calendar


class ConfirmacionReservaViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun guardarReservaFirebase(
        vueloIda: Vuelo,
        vueloVuelta: Vuelo?, // puede ser null
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

        // 🔎 Filtro básico de edad por año si fechaNacimiento es tipo "2005-12-31"
        val adultos = pasajeros.count {
            val birthDate = it.fechaNacimiento.toDate()
            val calNacimiento = Calendar.getInstance().apply { time = birthDate }
            val calHoy = Calendar.getInstance()

            val edad = calHoy.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR)
            if (calHoy.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad - 1
            } else {
                edad
            } >= 18
        }

        val menores = pasajeros.size - adultos


        val reserva = Reserva(
            idUsuario = uid,
            vuelos = listOfNotNull(vueloIda.id, vueloVuelta?.id),
            clase = clase,
            precioTotal = precioTotal,
            fechaReserva = Timestamp.now(),
            estado = true,
            adultos = adultos,
            menores = menores
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
