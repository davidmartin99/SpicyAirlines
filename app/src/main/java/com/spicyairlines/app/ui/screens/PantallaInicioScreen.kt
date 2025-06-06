package com.spicyairlines.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import com.spicyairlines.app.R

// Pantalla de Inicio de Autenticación (Login y Registro)
@Composable
fun PantallaInicioAuth(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        // Caja central con borde y fondo
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.53f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo de la aplicación
                    Icon(
                        painter = painterResource(id = R.drawable.logo_spicyairlines_2),
                        contentDescription = "Logo Spicy Airlines",
                        modifier = Modifier
                            .height(220.dp)
                            .padding(bottom = 12.dp),
                        tint = Color.Unspecified // <- MUY IMPORTANTE para mantener los colores originales del SVG
                    )

                    // Texto de bienvenida
                    Text(
                        text = "¡Explora los cielos de China con nosotros!",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Botón de Iniciar sesión
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Registrarse
                    OutlinedButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Registrarse")
                    }
                }
            }
        }
    }
}

// Vista previa para diseño
@Preview(showBackground = true)
@Composable
fun PantallaInicioAuthPreview() {
    AppTheme {
        PantallaInicioAuth(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
