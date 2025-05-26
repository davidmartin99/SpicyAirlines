package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.spicyairlines.app.viewmodel.RegisterViewModel

// Pantalla de Registro de Usuario
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    // Variables de estado obtenidas del ViewModel
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    val provincia by viewModel.provincia.collectAsState()
    val codigoPostal by viewModel.codigoPostal.collectAsState()
    val telefono by viewModel.telefono.collectAsState()

    // Errores individuales
    val error by viewModel.error.collectAsState()
    val errorNombre by viewModel.errorNombre.collectAsState()
    val errorApellidos by viewModel.errorApellidos.collectAsState()
    val errorCiudad by viewModel.errorCiudad.collectAsState()
    val errorProvincia by viewModel.errorProvincia.collectAsState()
    val errorCodigoPostal by viewModel.errorCodigoPostal.collectAsState()
    val errorTelefono by viewModel.errorTelefono.collectAsState()
    val errorPassword by viewModel.errorPassword.collectAsState()
    val nivelContrasena by viewModel.nivelContrasena.collectAsState()

    BasePantalla(onBack = onBack) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                // Campo de correo electrónico
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
            }

            item {
                // Campo de contraseña con indicador de seguridad
                PasswordTextFieldConCheckbox(
                    password = password,
                    onPasswordChange = { viewModel.onPasswordChange(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Indicador de nivel de seguridad
                if (password.isNotBlank()) {
                    LinearProgressIndicator(
                        progress = { nivelContrasena.valor },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = nivelContrasena.color
                    )

                    Text(
                        text = "Seguridad: ${nivelContrasena.descripcion}",
                        color = nivelContrasena.color,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    errorPassword?.let {
                        MensajeErrorConIcono(mensaje = it)
                    }
                }

            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Datos personales
            item {
                Text(text = "Datos personales", style = MaterialTheme.typography.titleMedium)
            }

            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorNombre?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item {
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { viewModel.onApellidosChange(it) },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorApellidos?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Dirección
            item {
                Text(text = "Dirección de contacto", style = MaterialTheme.typography.titleMedium)
            }

            item {
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { viewModel.onCiudadChange(it) },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorCiudad?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item {
                OutlinedTextField(
                    value = provincia,
                    onValueChange = { viewModel.onProvinciaChange(it) },
                    label = { Text("Provincia") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorProvincia?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item {
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { viewModel.onCodigoPostalChange(it) },
                    label = { Text("Código Postal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                errorCodigoPostal?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                errorTelefono?.let { MensajeErrorConIcono(mensaje = it) }
            }

            item {
                error?.let {
                    MensajeErrorConIcono(mensaje = it)
                }
            }

            // Botón de registro
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.register(onSuccess = onRegisterSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}
