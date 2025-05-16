package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Reserva
import com.spicyairlines.app.model.Vuelo
import java.util.*

// ViewModel para gestionar la confirmación y guardado de reservas en Firebase
class ConfirmacionReservaViewModel : ViewModel() {

    // Instancias de Firebase para la base de datos y autenticación
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Función para guardar una reserva en Firebase
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

        // Verifica que el ID del vuelo de ida sea válido
        if (vueloIda.id.isBlank()) {
            onFailure(Exception("ID de vuelo de ida no válido"))
            return
        }

        // Calcula el número de adultos y menores
        val menores = pasajeros.count { calcularEdad(it.fechaNacimiento.toDate()) < 3 }
        val adultos = pasajeros.size - menores

        // Prepara los IDs de los vuelos (ida y vuelta)
        val vueloIds = mutableListOf(vueloIda.id).apply {
            vueloVuelta?.id?.takeIf { it.isNotBlank() }?.let { add(it) }
        }

        // Crea el objeto de la reserva
        val reserva = Reserva(
            id = "",
            idUsuario = uid,
            vuelos = vueloIds,
            clase = clase,
            precioTotal = precioTotal,
            fechaReserva = Timestamp.now(),
            estado = true,
            adultos = adultos,
            menores = menores
        )

        // Guarda la reserva en Firebase
        db.collection("reservas")
            .add(reserva)
            .addOnSuccessListener { docRef ->
                val reservaId = docRef.id // ID generado automáticamente
                val reservaRef = db.collection("reservas").document(docRef.id)
                val batch = db.batch()

                // Guarda cada pasajero en una subcolección "pasajeros"
                pasajeros.forEach { pasajero ->
                    val pasajeroRef = reservaRef.collection("pasajeros").document() // Genera ID único
                    val pasajeroConId = pasajero.copy(id = pasajeroRef.id) // Asigna el ID generado al pasajero
                    batch.set(pasajeroRef, pasajeroConId)
                }

                // Actualiza el ID de la reserva
                batch.update(reservaRef, "id", reservaId)

                // Confirma los cambios
                batch.commit()
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

    // Función para calcular la edad de un pasajero
    private fun calcularEdad(fechaNacimiento: Date): Int {
        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }

        return edad
    }

    // Función para actualizar el número de asientos disponibles en el vuelo
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
