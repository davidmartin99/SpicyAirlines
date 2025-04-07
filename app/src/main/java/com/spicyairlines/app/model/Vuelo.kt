package com.spicyairlines.app.model

import com.google.firebase.Timestamp

data class Vuelo(
    val id: String = "",
    val origen: String = "",
    val destino: String = "",
    val fechaSalida: Timestamp = Timestamp.now(),
    val fechaLlegada: Timestamp = Timestamp.now(),
    val duracion: String = "",
    val temporada: String = "",
    val precioBase: Int = 0,
    val asientosTurista: Int = 0,
    val asientosPremium: Int = 0,
    val asientosBusiness: Int = 0
)
