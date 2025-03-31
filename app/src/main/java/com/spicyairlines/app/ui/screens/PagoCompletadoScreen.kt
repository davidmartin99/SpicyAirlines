package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.components.BasePantalla

@Composable
fun PagoCompletadoScreen(
    onVolverInicio: () -> Unit
) {
    BasePantalla(title = "¡Reserva confirmada!") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉 Tu reserva se ha completado exitosamente.",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onVolverInicio) {
                Text("Volver al inicio")
            }
        }
    }
}
