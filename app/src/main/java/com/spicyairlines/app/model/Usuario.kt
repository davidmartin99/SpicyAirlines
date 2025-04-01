package com.spicyairlines.app.model

data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val ciudad: String = "",
    val provincia: String = "",
    val codigoPostal: String = "",
    val telefono: String = ""
)
