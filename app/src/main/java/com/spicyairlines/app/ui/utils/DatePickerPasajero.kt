package com.spicyairlines.app.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
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
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) } // 0-based
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val daysInMonth = remember(selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val years = (minYear..maxYear).toList().reversed()
    val days = (1..daysInMonth).toList()

    LaunchedEffect(selectedYear, selectedMonth, selectedDay) {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
        if (cal.time <= today.time) {
            onDateSelected(cal.time)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Año
            DropdownSelector(
                label = "Año",
                items = years.map { it.toString() },
                selectedItem = selectedYear.toString(),
                onItemSelected = { selectedYear = it.toInt() },
                modifier = Modifier.weight(1f)
            )

            // Mes
            DropdownSelector(
                label = "Mes",
                items = months,
                selectedItem = months[selectedMonth],
                onItemSelected = { selectedMonth = months.indexOf(it) },
                modifier = Modifier.weight(1f)
            )

            // Día
            DropdownSelector(
                label = "Día",
                items = days.map { it.toString() },
                selectedItem = selectedDay.toString(),
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
            modifier = Modifier.menuAnchor()
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
