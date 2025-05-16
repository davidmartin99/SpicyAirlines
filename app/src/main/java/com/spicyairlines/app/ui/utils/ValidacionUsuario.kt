package com.spicyairlines.app.utils

// Clase de datos que representa el resultado de una validación de usuario
data class ValidacionUsuario(
    val esValido: Boolean,
    val mensajeError: String? = null
)

// Función que valida los campos de un usuario
fun validarCamposUsuario(
    nombre: String,
    apellidos: String,
    ciudad: String,
    provincia: String,
    codigoPostal: String,
    telefono: String,
    password: String? = null // Contraseña opcional (solo se valida si no es nula)
): ValidacionUsuario {
    // Expresión regular para validar campos de texto (letras, espacios, guiones, tildes)
    val regexTexto = Regex("^[\\p{L}\\s'-]{2,50}$") // permite letras con tildes y ñ, espacios, apóstrofes y guiones
    // Expresión regular para validar contraseñas fuertes
    val regexPasswordFuerte = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")

    // Verifica que todos los campos obligatorios no estén en blanco
    if (nombre.isBlank() || apellidos.isBlank() || ciudad.isBlank() || provincia.isBlank()) {
        return ValidacionUsuario(false, "Todos los campos son obligatorios")
    }

    // Verifica que el nombre cumpla el formato permitido
    if (!regexTexto.matches(nombre)) {
        return ValidacionUsuario(false, "El nombre contiene caracteres inválidos")
    }

    // Verifica que los apellidos cumplan el formato permitido
    if (!regexTexto.matches(apellidos)) {
        return ValidacionUsuario(false, "Los apellidos contienen caracteres inválidos")
    }

    // Verifica que la ciudad cumpla el formato permitido
    if (!regexTexto.matches(ciudad)) {
        return ValidacionUsuario(false, "La ciudad contiene caracteres inválidos")
    }

    // Verifica que la provincia cumpla el formato permitido
    if (!regexTexto.matches(provincia)) {
        return ValidacionUsuario(false, "La provincia contiene caracteres inválidos")
    }

    // Verifica que el código postal tenga 5 dígitos
    if (codigoPostal.length != 5 || !codigoPostal.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El código postal debe tener 5 dígitos")
    }

    // Verifica que el teléfono tenga 9 dígitos
    if (telefono.length != 9 || !telefono.all { it.isDigit() }) {
        return ValidacionUsuario(false, "El teléfono debe tener 9 dígitos")
    }

    // Verifica la seguridad de la contraseña solo si no es nula
    if (!password.isNullOrBlank() && !regexPasswordFuerte.matches(password)) {
        return ValidacionUsuario(false, "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo")
    }

    // Si todas las validaciones son correctas
    return ValidacionUsuario(true)
}
