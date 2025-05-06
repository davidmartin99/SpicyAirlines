package com.spicyairlines.app.ui.utils

import androidx.compose.ui.graphics.Color

enum class NivelSeguridadContrasena(
    val descripcion: String,
    val color: Color,
    val valor: Float
) {
    MUY_DEBIL("Muy débil", Color(0xFFF44336), 0.1f),
    DEBIL("Débil", Color(0xFFFF9800), 0.3f),
    NORMAL("Normal", Color(0xFFFFEB3B), 0.5f),
    BUENA("Buena", Color(0xFF7EAF4C), 0.75f),
    EXCELENTE("Excelente", Color(0xFF2E7D32), 1f)
}
