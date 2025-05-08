package com.spicyairlines.app.utils

import com.spicyairlines.app.model.Pasajero
import java.util.*

data class ValidacionPasajero(
    val esValido: Boolean,
    val mensajeError: String? = null
)

fun validarPasajero(pasajero: Pasajero): ValidacionPasajero {
    if (pasajero.nombre.isBlank()) {
        return ValidacionPasajero(false, "Nombre inválido")
    }

    if (pasajero.apellidos.isBlank()) {
        return ValidacionPasajero(false, "Apellidos inválidos")
    }

    val pasaporteRegex = Regex("^[A-Z]{3}\\d{6}$")
    if (!pasaporteRegex.matches(pasajero.numeroPasaporte)) {
        return ValidacionPasajero(false, "Número de pasaporte inválido")
    }

    if (pasajero.telefono.length !=9  || !pasajero.telefono.all { it.isDigit() }) {
        return ValidacionPasajero(false, "Número de teléfono inválido")
    }

    val hoy = Calendar.getInstance()
    val nacimiento = Calendar.getInstance().apply {
        time = pasajero.fechaNacimiento.toDate()
    }

    if (nacimiento.after(hoy)) {
        return ValidacionPasajero(false, "Fecha de nacimiento inválida")
    }

    val edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR) -
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) 1 else 0

    if (edad < 0) {
        return ValidacionPasajero(false, "Edad inválida")
    }

    return ValidacionPasajero(true)
}
