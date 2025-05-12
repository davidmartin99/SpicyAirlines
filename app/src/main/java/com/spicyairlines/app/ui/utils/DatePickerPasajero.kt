package com.spicyairlines.app.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun DatePickerPasajero(
    label: String,
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate

    val today = Calendar.getInstance()
    val minYear = today.get(Calendar.YEAR) - 140
    val maxYear = today.get(Calendar.YEAR)

    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    // Rango de años
    val years = (minYear..maxYear).toList().reversed()

    // Actualizamos el calendario según los valores seleccionados
    LaunchedEffect(selectedYear, selectedMonth, selectedDay) {
        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
        if (calendar.time <= today.time) {
            onDateSelected(calendar.time)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Año
            DropdownSelector(
                label = "Año",
                items = years.map { it.toString() },
                selectedItem = selectedYear.toString(),
                onItemSelected = { selectedYear = it.toInt() },
                modifier = Modifier.weight(1f)
            )

            // Mes (en texto)
            DropdownSelector(
                label = "Mes",
                items = (1..12).map { String.format("%02d", it) },
                selectedItem = String.format("%02d", selectedMonth + 1),
                onItemSelected = { selectedMonth = it.toInt() - 1 },
                modifier = Modifier.weight(1f)
            )

            // Días del mes (ajustados)
            val daysInMonth = remember(selectedYear, selectedMonth) {
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

            val days = (1..daysInMonth).map { it.toString().padStart(2, '0') }

            DropdownSelector(
                label = "Día",
                items = days,
                selectedItem = String.format("%02d", selectedDay),
                onItemSelected = { selectedDay = it.toInt() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
