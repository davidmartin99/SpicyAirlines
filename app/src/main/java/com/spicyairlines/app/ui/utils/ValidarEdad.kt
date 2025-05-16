package com.spicyairlines.app.utils

import com.spicyairlines.app.model.Pasajero
import java.util.*

// Función para calcular la edad de un pasajero basado en su fecha de nacimiento
fun calcularEdad(pasajero: Pasajero): Int {
    val hoy = Calendar.getInstance() // Obtiene la fecha actual
    val nacimiento = Calendar.getInstance().apply {
        time = pasajero.fechaNacimiento.toDate() // Configura la fecha de nacimiento del pasajero
    }
    // Calcula la edad restando los años
    var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
    // Ajusta la edad si la fecha de hoy es antes del cumpleaños de este año
    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--
    // Devuelve la edad
    return edad
}

// Verifica si las edades de los pasajeros coinciden con la cantidad de adultos y niños esperados
fun edadesSonValidas(
    pasajeros: List<Pasajero>,
    adultosEsperados: Int, // Número de adultos esperados
    ninosEsperados: Int   // Número de niños esperados
): Boolean {
    // Calcula la cantidad real de adultos y niños
    val adultosReales = pasajeros.count { calcularEdad(it) >= 3 }
    val ninosReales = pasajeros.count { calcularEdad(it) < 3 }
    // Compara las cantidades reales con las esperadas
    return adultosReales == adultosEsperados && ninosReales == ninosEsperados
}
