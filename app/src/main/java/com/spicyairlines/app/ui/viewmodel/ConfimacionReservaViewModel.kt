package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Tasks
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.Vuelo
import java.util.*

class ConfirmacionReservaViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun guardarReservaFirebase(
        vueloIda: Vuelo,
        vueloVuelta: Vuelo?,
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

        if (vueloIda.id.isBlank()) {
            onFailure(Exception("ID de vuelo de ida no válido"))
            return
        }

        val menores = pasajeros.count { calcularEdad(it.fechaNacimiento.toDate()) < 3 }
        val adultos = pasajeros.size - menores

        val vueloIds = mutableListOf(vueloIda.id).apply {
            vueloVuelta?.id?.takeIf { it.isNotBlank() }?.let { add(it) }
        }

        val reserva = Reserva(
            idUsuario = uid,
            vuelos = vueloIds,
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
                val reservaRef = db.collection("reservas").document(docRef.id)
                val tareas = pasajeros.map { reservaRef.collection("pasajeros").add(it) }

                Tasks.whenAllComplete(tareas)
                    .addOnSuccessListener {
                        actualizarAsientos(vueloIda, clase, pasajeros.size, onFailure)
                        vueloVuelta?.let {
                            actualizarAsientos(it, clase, pasajeros.size, onFailure)
                        }
                        onSuccess()
                    }
                    .addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }

    private fun calcularEdad(fechaNacimiento: Date): Int {
        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }

        return edad
    }

    private fun actualizarAsientos(
        vuelo: Vuelo,
        clase: String,
        cantidad: Int,
        onError: (Exception) -> Unit
    ) {
        val vueloRef = db.collection("Vuelos").document(vuelo.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(vueloRef)
            val campo = when (clase) {
                "Business" -> "asientosBusiness"
                "Premium" -> "asientosPremium"
                else -> "asientosTurista"
            }

            val actuales = snapshot.getLong(campo) ?: 0
            transaction.update(vueloRef, campo, actuales - cantidad)
        }.addOnFailureListener(onError)
    }
}
