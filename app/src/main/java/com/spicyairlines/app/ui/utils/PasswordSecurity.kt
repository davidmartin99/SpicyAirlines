package com.spicyairlines.app.ui.utils

import androidx.compose.ui.graphics.Color

// Enumeración que define los diferentes niveles de seguridad de una contraseña
enum class NivelSeguridadContrasena(
    val descripcion: String, // Descripción del nivel de seguridad
    val color: Color, // Color asociado al nivel para la interfaz de usuario
    val valor: Float // Valor numérico asociado al nivel (0.0 a 1.0)
) {
    MUY_DEBIL("Muy débil", Color(0xFFF44336), 0.1f),
    DEBIL("Débil", Color(0xFFFF9800), 0.3f),
    NORMAL("Normal", Color(0xFFFFEB3B), 0.5f),
    BUENA("Buena", Color(0xFF7EAF4C), 0.75f),
    EXCELENTE("Excelente", Color(0xFF2E7D32), 1f)
}
