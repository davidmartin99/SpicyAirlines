package com.spicyairlines.app.utils

data class ValidacionUsuario(
    val esValido: Boolean,
    val mensajeError: String? = null
)

fun validarCamposUsuario(
    nombre: String,
    apellidos: String,
    ciudad: String,
    provincia: String,
    codigoPostal: String,
    telefono: String,
    password: String? = null
): ValidacionUsuario {
    if (nombre.isBlank() || apellidos.isBlank() || ciudad.isBlank() || provincia.isBlank()) {
        return ValidacionUsuario(false, "Todos los campos son obligatorios")
    }

    if (codigoPostal.length != 5 || !codigoPostal.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El código postal debe tener 5 dígitos")
    }

    if (telefono.length != 9 || !telefono.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El teléfono debe tener 9 dígitos")
    }

    if (!password.isNullOrBlank() && password.length < 6) {
        return ValidacionUsuario(false, "La contraseña debe tener al menos 6 caracteres")
    }

    return ValidacionUsuario(true)
}
