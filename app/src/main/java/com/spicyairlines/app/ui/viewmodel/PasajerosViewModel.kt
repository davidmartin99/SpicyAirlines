package com.spicyairlines.app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.spicyairlines.app.model.Pasajero
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class PasajerosViewModel : ViewModel() {

    var numPasajeros = 1 // Este valor debería actualizarse según la reserva
    var pasajeros = mutableStateListOf<Pasajero>()

    init {
        // Inicializa lista de pasajeros con valores vacíos
        repeat(numPasajeros) {
            pasajeros.add(Pasajero())
        }
    }

    fun actualizarPasajero(index: Int, valor: String, campo: String) {
        val pasajero = pasajeros[index]
        when (campo) {
            "nombre" -> pasajeros[index] = pasajero.copy(nombre = valor)
            "apellidos" -> pasajeros[index] = pasajero.copy(apellidos = valor)
            "fechaNacimiento" -> pasajeros[index] = pasajero.copy(fechaNacimiento = convertirStringToTimestamp(valor))
            "numeroPasaporte" -> pasajeros[index] = pasajero.copy(numeroPasaporte = valor)
            "telefono" -> pasajeros[index] = pasajero.copy(telefono = valor)
        }
    }

    // Función para convertir el String a Timestamp
    private fun convertirStringToTimestamp(fecha: String): Timestamp {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = formato.parse(fecha) ?: Date() // Si la fecha es inválida, usamos la fecha actual
        return Timestamp(date)
    }
}
