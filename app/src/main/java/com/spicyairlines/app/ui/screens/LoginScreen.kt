package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.ui.components.PasswordTextFieldConCheckbox
import com.spicyairlines.app.ui.components.MensajeErrorConIcono

// Pantalla de Inicio de Sesión (Login)
@Composable
fun LoginScreen(
    viewModel: com.spicyairlines.app.viewmodel.LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    // Variables de estado obtenidas del ViewModel
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    // Habilita el botón solo si los campos no están vacíos
    val isLoginEnabled = email.isNotBlank() && password.isNotBlank()

    BasePantalla(
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo para el correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de texto para la contraseña con opción de mostrar/ocultar
            PasswordTextFieldConCheckbox(
                password = password,
                onPasswordChange = { viewModel.onPasswordChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                // Botón de inicio de sesión
                Button(
                    onClick = {
                        viewModel.login(email, password, onLoginSuccess)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isLoginEnabled
                ) {
                    Text("Entrar")
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                MensajeErrorConIcono(mensaje = it)
            }
        }
    }
}
