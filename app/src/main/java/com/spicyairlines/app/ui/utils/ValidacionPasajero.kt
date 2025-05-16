package com.spicyairlines.app.utils

import com.spicyairlines.app.model.Pasajero
import java.util.*

// Clase de datos que representa el resultado de la validación de un pasajero
data class ValidacionPasajero(
    val esValido: Boolean,
    val mensajeError: String? = null
)

// Función que valida los campos de un pasajero
fun validarPasajero(pasajero: Pasajero): ValidacionPasajero {
    // Verifica que el nombre no esté en blanco
    if (pasajero.nombre.isBlank()) {
        return ValidacionPasajero(false, "Nombre inválido")
    }

    // Verifica que los apellidos no estén en blanco
    if (pasajero.apellidos.isBlank()) {
        return ValidacionPasajero(false, "Apellidos inválidos")
    }

    // Verifica que el número de pasaporte tenga el formato correcto (3 letras + 6 dígitos)
    val pasaporteRegex = Regex("^[A-Z]{3}\\d{6}$")
    if (!pasaporteRegex.matches(pasajero.numeroPasaporte)) {
        return ValidacionPasajero(false, "Número de pasaporte inválido")
    }

    // Verifica que el teléfono tenga 9 dígitos y solo números
    if (pasajero.telefono.length !=9  || !pasajero.telefono.all { it.isDigit() }) {
        return ValidacionPasajero(false, "Número de teléfono inválido")
    }

    // Verifica que la fecha de nacimiento no sea futura
    val hoy = Calendar.getInstance()
    val nacimiento = Calendar.getInstance().apply {
        time = pasajero.fechaNacimiento.toDate()
    }

    if (nacimiento.after(hoy)) {
        return ValidacionPasajero(false, "Fecha de nacimiento inválida")
    }

    // Calcula la edad del pasajero
    val edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR) -
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) 1 else 0

    // Verifica que la edad sea válida (no negativa)
    if (edad < 0) {
        return ValidacionPasajero(false, "Edad inválida")
    }

    // Si todas las validaciones son correctas
    return ValidacionPasajero(true)
}
