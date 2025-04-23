package com.spicyairlines.app.utils

import com.spicyairlines.app.model.Pasajero
import java.util.*

fun calcularEdad(pasajero: Pasajero): Int {
    val hoy = Calendar.getInstance()
    val nacimiento = Calendar.getInstance().apply {
        time = pasajero.fechaNacimiento.toDate()
    }
    var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--
    return edad
}

fun edadesSonValidas(
    pasajeros: List<Pasajero>,
    adultosEsperados: Int,
    ninosEsperados: Int
): Boolean {
    val adultosReales = pasajeros.count { calcularEdad(it) >= 3 }
    val ninosReales = pasajeros.count { calcularEdad(it) < 3 }
    return adultosReales == adultosEsperados && ninosReales == ninosEsperados
}
