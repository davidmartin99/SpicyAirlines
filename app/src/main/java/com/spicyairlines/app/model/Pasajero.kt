package com.spicyairlines.app.model

import com.google.firebase.Timestamp

data class Pasajero(
    val nombre: String = "",
    val apellidos: String = "",
    val fechaNacimiento: Timestamp = Timestamp.now(),
    val numeroPasaporte: String = "",
    val telefono: String = ""
)
