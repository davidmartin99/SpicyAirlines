package com.spicyairlines.app.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object HoraUTC {

    fun formatearFechaHoraLocal(timestamp: Timestamp, ciudad: String): String {
        val zona = when (ciudad) {
            "Madrid" -> "Europe/Madrid"
            "Chongqing", "Chengdu" -> "Asia/Shanghai"
            else -> "UTC"
        }

        val instant = timestamp.toDate().toInstant()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of(zona))

        return formatter.format(instant)
    }

}
