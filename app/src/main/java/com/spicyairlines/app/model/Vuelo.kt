package com.spicyairlines.app.model

data class Vuelo(
    val id: String = "",
    val origen: String = "Madrid",
    val ciudadDestino: String = "",
    val fechaIda: String = "",
    val fechaVuelta: String = "",
    val horaSalidaIda: String = "",
    val horaLlegada: String = "",
    val horaSalidaVuelta: String = "",
    val horaLlegadaVuelta: String = "",
    val duracionIda: String = "",
    val duracionVuelta: String = "",
    val temporada: String = "baja",
    val precioBase: Int = 0,
    val asientosTurista: Int = 300,
    val asientosPremium: Int = 100,
    val asientosBusiness: Int = 50
)
