package com.spicyairlines.app.model

data class ReservaConVuelo(
    val reserva: Reserva = Reserva(),
    val vuelo: Vuelo = Vuelo()
)
