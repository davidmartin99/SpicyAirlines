package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class DatosPasajerosViewModel : ViewModel() {

    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    private val db = FirebaseFirestore.getInstance()

    fun inicializarFormularios(numPasajeros: Int) {
        if (_pasajeros.value.isEmpty()) {
            _pasajeros.value = List(numPasajeros) { Pasajero() }
        }
    }

    fun actualizarCampo(index: Int, campo: String, valor: String) {
        val lista = _pasajeros.value.toMutableList()
        if (index !in lista.indices) return

        val pasajero = lista[index]

        lista[index] = when (campo) {
            "nombre" -> pasajero.copy(nombre = valor)
            "apellidos" -> pasajero.copy(apellidos = valor)
            "numeroPasaporte" -> pasajero.copy(numeroPasaporte = valor)
            "telefono" -> pasajero.copy(telefono = valor)
            else -> pasajero
        }

        _pasajeros.value = lista
    }

    fun actualizarFechaNacimiento(index: Int, fecha: Date) {
        val lista = _pasajeros.value.toMutableList()
        if (index in lista.indices) {
            lista[index] = lista[index].copy(fechaNacimiento = Timestamp(fecha))
            _pasajeros.value = lista
        }
    }

    fun guardarPasajeros(reservaId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val pasajerosList = _pasajeros.value
        val batch = db.batch()
        val reservaRef = db.collection("reservas").document(reservaId)

        pasajerosList.forEach { pasajero ->
            val nuevoPasajeroRef = reservaRef.collection("pasajeros").document()
            batch.set(nuevoPasajeroRef, pasajero)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
