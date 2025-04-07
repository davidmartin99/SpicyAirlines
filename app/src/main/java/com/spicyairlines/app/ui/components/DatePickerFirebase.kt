package com.spicyairlines.app.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerFirebase(
    label: String,
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var selectedDate by remember {
        mutableStateOf(dateFormat.format(initialDate))
    }

    fun showDatePickerDialog(context: Context) {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            val date = calendar.time
            selectedDate = dateFormat.format(date)
            onDateSelected(date)
        }

        val dateParts = selectedDate.split("/").mapNotNull { it.toIntOrNull() }
        if (dateParts.size == 3) {
            calendar.set(dateParts[2], dateParts[1] - 1, dateParts[0])
        }

        DatePickerDialog(
            context,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePickerDialog(context) }
    )
}
