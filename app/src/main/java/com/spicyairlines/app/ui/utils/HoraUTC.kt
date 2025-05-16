package com.spicyairlines.app.ui.utils

import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Objeto utilitario para manejar la conversión y formato de fechas/hora UTC a hora local
object HoraUTC {

    // Función para formatear una fecha/hora (Timestamp) a la hora local de la ciudad especificada
    fun formatearFechaHoraLocal(timestamp: Timestamp, ciudad: String): String {
        // Determina la zona horaria según la ciudad proporcionada
        val zona = when (ciudad) {
            "Madrid" -> "Europe/Madrid"
            "Chongqing", "Chengdu" -> "Asia/Shanghai"
            else -> "UTC"
        }

        // Convierte el Timestamp (Firebase) a Instant
        val instant = timestamp.toDate().toInstant()
        // Formatea la fecha y hora según el formato especificado y la zona horaria
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of(zona))

        // Devuelve la fecha/hora formateada
        return formatter.format(instant)
    }

}
