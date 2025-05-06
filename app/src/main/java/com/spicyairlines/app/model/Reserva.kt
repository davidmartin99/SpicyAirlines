package com.spicyairlines.app.model

import com.google.firebase.Timestamp

data class Reserva(
    val id: String = "",
    val idUsuario: String = "",
    val vuelos: List<String> = emptyList(),
    val clase: String = "",
    val precioTotal: Double = 0.0,
    val fechaReserva: Timestamp = Timestamp.now(),
    val estado: Boolean = true,
    val adultos: Int = 0,
    val menores: Int = 0
)

