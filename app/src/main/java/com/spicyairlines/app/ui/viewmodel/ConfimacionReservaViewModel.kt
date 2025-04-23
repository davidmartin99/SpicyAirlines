package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.android.gms.tasks.Tasks
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.Vuelo
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

        if (vueloIda.id.isBlank()) {
            onFailure(Exception("ID de vuelo de ida no válido"))
            return
        }

        val vueloIds = mutableListOf<String>().apply {
            add(vueloIda.id)
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
                println("✅ Reserva guardada con ID: ${docRef.id}")
                val reservaRef = db.collection("reservas").document(docRef.id)
                val tareas = pasajeros.map { pasajero ->
                    reservaRef.collection("pasajeros").add(pasajero)
                }

                Tasks.whenAllComplete(tareas)
                    .addOnSuccessListener {
                        println("✅ Todos los pasajeros guardados correctamente.")

                        // ✅ Actualizar asientos disponibles en ambos vuelos
                        actualizarAsientosDisponibles(vueloIda, clase, pasajeros.size, onFailure)
                        vueloVuelta?.let {
                            actualizarAsientosDisponibles(it, clase, pasajeros.size, onFailure)
                        }

                        onSuccess()
                    }
                    .addOnFailureListener {
                        println("❌ Error al guardar pasajeros: ${it.message}")
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                println("❌ Error al guardar reserva: ${it.message}")
                onFailure(it)
            }
    }

    private fun actualizarAsientosDisponibles(
        vuelo: Vuelo,
        clase: String,
        cantidad: Int,
        onError: (Exception) -> Unit
    ) {
        val vueloRef = db.collection("vuelos").document(vuelo.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(vueloRef)
            when (clase) {
                "Business" -> {
                    val actuales = snapshot.getLong("asientosBusiness") ?: 0
                    transaction.update(vueloRef, "asientosBusiness", actuales - cantidad)
                }
                "Premium" -> {
                    val actuales = snapshot.getLong("asientosPremium") ?: 0
                    transaction.update(vueloRef, "asientosPremium", actuales - cantidad)
                }
                else -> {
                    val actuales = snapshot.getLong("asientosTurista") ?: 0
                    transaction.update(vueloRef, "asientosTurista", actuales - cantidad)
                }
            }
        }.addOnFailureListener {
            println("❌ Error al actualizar asientos: ${it.message}")
            onError(it)
        }
    }
}
