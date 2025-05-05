package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.spicyairlines.app.utils.validarCamposUsuario

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    val provincia by viewModel.provincia.collectAsState()
    val codigoPostal by viewModel.codigoPostal.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val error by viewModel.error.collectAsState()

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
                PasswordTextFieldConCheckbox(
                    password = password,
                    onPasswordChange = { viewModel.onPasswordChange(it) }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

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
            }

            item {
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { viewModel.onApellidosChange(it) },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

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
            }

            item {
                OutlinedTextField(
                    value = provincia,
                    onValueChange = { viewModel.onProvinciaChange(it) },
                    label = { Text("Provincia") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { viewModel.onCodigoPostalChange(it) },
                    label = { Text("Código Postal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            item {
                error?.let {
                    MensajeErrorConIcono(mensaje = it)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val resultado = validarCamposUsuario(
                            nombre = nombre,
                            apellidos = apellidos,
                            ciudad = ciudad,
                            provincia = provincia,
                            codigoPostal = codigoPostal,
                            telefono = telefono,
                            password = password
                        )

                        if (!resultado.esValido) {
                            viewModel.setError(resultado.mensajeError ?: "Error de validación")
                            return@Button
                        }

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
