package com.spicyairlines.app.model

data class Vuelo(
    val id: String = "", // ID del documento en Firebase
    val origen: String = "Madrid",
    val destino: String = "",
    val fechaSalida: String = "", // Formato: "2025-07-15"
    val fechaVuelta: String = "", // Formato: "2025-07-30"
    val horaSalidaIda: String = "", // Ej: "10:30"
    val horaLlegadaIda: String = "", // Ej: "15:00"
    val horaSalidaVuelta: String = "", // Ej: "12:00"
    val horaLlegadaVuelta: String = "", // Ej: "17:00"
    val temporada: String = "baja", // baja, media, alta
    val precioBase: Double = 0.0,

    val asientosTuristaDisponibles: Int = 300,
    val asientosPremiumDisponibles: Int = 100,
    val asientosBusinessDisponibles: Int = 50
)
