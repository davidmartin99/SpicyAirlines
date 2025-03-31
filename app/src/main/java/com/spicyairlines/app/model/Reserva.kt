package com.spicyairlines.app.model

data class Reserva(
    val uidUsuario: String = "",
    val vueloId: String = "",
    val clase: String = "",
    val pasajeros: List<Pasajero> = emptyList(),
    val precioTotal: Double = 0.0,
    val fechaReserva: String = "",
    val estado: Boolean = true
)
