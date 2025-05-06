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
    val regexTexto = Regex("^[\\p{L}\\s'-]{2,50}$") // permite letras con tildes y ñ, espacios, apóstrofes y guiones
    // Obliga a tener mayúscula, minúscula, número, y un carácter especial y un mínimo de 8 caracteres.
    val regexPasswordFuerte = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")

    if (nombre.isBlank() || apellidos.isBlank() || ciudad.isBlank() || provincia.isBlank()) {
        return ValidacionUsuario(false, "Todos los campos son obligatorios")
    }

    if (!regexTexto.matches(nombre)) {
        return ValidacionUsuario(false, "El nombre contiene caracteres inválidos")
    }

    if (!regexTexto.matches(apellidos)) {
        return ValidacionUsuario(false, "Los apellidos contienen caracteres inválidos")
    }

    if (!regexTexto.matches(ciudad)) {
        return ValidacionUsuario(false, "La ciudad contiene caracteres inválidos")
    }

    if (!regexTexto.matches(provincia)) {
        return ValidacionUsuario(false, "La provincia contiene caracteres inválidos")
    }

    if (codigoPostal.length != 5 || !codigoPostal.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El código postal debe tener 5 dígitos")
    }

    if (telefono.length != 9 || !telefono.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El teléfono debe tener 9 dígitos")
    }

    if (!password.isNullOrBlank() && !regexPasswordFuerte.matches(password)) {
        return ValidacionUsuario(false, "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo")
    }


    return ValidacionUsuario(true)
}
