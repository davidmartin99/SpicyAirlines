package com.spicyairlines.app.ui.utils

import com.google.firebase.Timestamp
import java.util.*

fun Timestamp.plusDays(days: Int): Timestamp {
    // Sumamos 2 días en milisegundos
    val millisPerDay = 86400000L
    return Timestamp(Date(this.toDate().time + days * millisPerDay))
}
