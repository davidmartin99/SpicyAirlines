package com.spicyairlines.app.ui.utils

import com.google.firebase.Timestamp
import java.util.*

// Extensión para la clase Timestamp que permite sumar días
fun Timestamp.plusDays(days: Int): Timestamp {
    // Definimos el número de milisegundos en un día (24 horas)
    val millisPerDay = 86400000L

    // Calculamos la nueva fecha sumando los milisegundos correspondientes
    return Timestamp(Date(this.toDate().time + days * millisPerDay))
}
