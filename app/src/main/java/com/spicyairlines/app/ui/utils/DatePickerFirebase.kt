package com.spicyairlines.app.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.spicyairlines.app.R
import com.spicyairlines.app.ui.components.MensajeErrorConIcono
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable

// Composable para seleccionar fechas (ida y vuelta) utilizando DatePickerDialog
@Composable
fun DatePickerFirebase(
    soloIda: Boolean, // Indica si es un vuelo solo de ida
    fechaIda: Timestamp?, // Fecha de ida seleccionada
    fechaVuelta: Timestamp?, // Fecha de vuelta seleccionada (si aplica)
    onFechasSeleccionadas: (Timestamp, Timestamp?) -> Unit // Callback para devolver las fechas seleccionadas
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Texto del botón que muestra las fechas seleccionadas
    var textoBoton by remember {
        mutableStateOf(
            when {
                soloIda && fechaIda != null -> "Ida: ${dateFormat.format(fechaIda.toDate())}"
                !soloIda && fechaIda != null && fechaVuelta != null ->
                    "Ida: ${dateFormat.format(fechaIda.toDate())} - Vuelta: ${dateFormat.format(fechaVuelta.toDate())}"
                else -> "Seleccionar fecha(s)"
            }
        )
    }

    // Variable para manejar mensajes de error
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Botón para seleccionar fechas
        Button(
            onClick = {
                if (soloIda) { // Si es solo ida
                    showDatePicker(context) { ida ->
                        textoBoton = "Ida: ${dateFormat.format(ida)}"
                        error = null
                        onFechasSeleccionadas(Timestamp(ida), null)
                    }
                } else { // Si es ida y vuelta
                    showDatePicker(context) { ida ->
                        showDatePicker(context, minDate = ida.time + 2 * 86400000) { vuelta ->
                            val dias = (vuelta.time - ida.time) / (1000 * 60 * 60 * 24)
                            if (dias in 2..60) {
                                textoBoton =
                                    "Ida: ${dateFormat.format(ida)} - Vuelta: ${dateFormat.format(vuelta)}"
                                error = null
                                onFechasSeleccionadas(Timestamp(ida), Timestamp(vuelta))
                            } else {
                                error = "El rango debe ser entre 2 y 60 días."
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendario"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(textoBoton)
        }

        // Muestra mensaje de error si existe
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            MensajeErrorConIcono(mensaje = it)
        }
    }
}

// Función auxiliar para mostrar el DatePickerDialog
private fun showDatePicker(
    context: Context,
    minDate: Long? = null,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // Establece la fecha mínima
    minDate?.let { dialog.datePicker.minDate = it }
    dialog.show()
}
