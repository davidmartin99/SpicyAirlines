package com.spicyairlines.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.R
import com.spicyairlines.app.components.BasePantalla

// Pantalla de Confirmación de Pago Completado
@Composable
fun PagoCompletadoScreen(
    onVolverInicio: () -> Unit,
) {
    BasePantalla {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen de confirmación de pago
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "Reserva completada",
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de éxito
            Text(
                text = "Tu reserva se ha completado exitosamente",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver al inicio
            Button(
                onClick = onVolverInicio,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
