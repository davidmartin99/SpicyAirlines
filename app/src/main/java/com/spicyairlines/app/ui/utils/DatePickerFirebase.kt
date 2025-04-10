package com.spicyairlines.app.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.spicyairlines.app.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerFirebase(
    soloIda: Boolean,
    fechaIda: Timestamp?,
    fechaVuelta: Timestamp?,
    onFechasSeleccionadas: (Timestamp, Timestamp?) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var textoBoton by remember {
        mutableStateOf(
            if (soloIda && fechaIda != null) "Ida: ${dateFormat.format(fechaIda.toDate())}"
            else if (!soloIda && fechaIda != null && fechaVuelta != null)
                "Ida: ${dateFormat.format(fechaIda.toDate())} - Vuelta: ${dateFormat.format(fechaVuelta.toDate())}"
            else "Seleccionar fecha(s)"
        )
    }

    Button(
        onClick = {
            if (soloIda) {
                showDatePicker(context) { ida ->
                    textoBoton = "Ida: ${dateFormat.format(ida)}"
                    onFechasSeleccionadas(Timestamp(ida), null)
                }
            } else {
                showDatePicker(context) { ida ->
                    showDatePicker(context, minDate = ida.time + 2 * 86400000) { vuelta ->
                        val dias = (vuelta.time - ida.time) / (1000 * 60 * 60 * 24)
                        if (dias in 2..60) {
                            textoBoton = "Ida: ${dateFormat.format(ida)} - Vuelta: ${dateFormat.format(vuelta)}"
                            onFechasSeleccionadas(Timestamp(ida), Timestamp(vuelta))
                        } else {
                            Toast.makeText(context, "❌ Rango permitido: entre 2 y 60 días", Toast.LENGTH_LONG).show()
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
}

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
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    minDate?.let { dialog.datePicker.minDate = it }
    dialog.show()
}
