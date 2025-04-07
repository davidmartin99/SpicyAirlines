package com.spicyairlines.app.model

import com.google.firebase.Timestamp

data class Pasajero(
    var nombre: String = "",
    var apellidos: String = "",
    var fechaNacimiento: Timestamp = Timestamp.now(), // obligatorio, no null
    var numeroPasaporte: String = "",
    var telefono: String = ""
)
