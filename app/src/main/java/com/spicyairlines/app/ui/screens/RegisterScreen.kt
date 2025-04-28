package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.RegisterViewModel
import androidx.compose.ui.text.input.*
import com.spicyairlines.app.ui.components.PasswordTextFieldConCheckbox
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

    BasePantalla(
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            PasswordTextFieldConCheckbox(
                password = password,
                onPasswordChange = { viewModel.onPasswordChange(it) }
            )

            OutlinedTextField(value = nombre, onValueChange = { viewModel.onNombreChange(it) }, label = { Text("Nombre") })
            OutlinedTextField(value = apellidos, onValueChange = { viewModel.onApellidosChange(it) }, label = { Text("Apellidos") })
            OutlinedTextField(value = ciudad, onValueChange = { viewModel.onCiudadChange(it) }, label = { Text("Ciudad") })
            OutlinedTextField(value = provincia, onValueChange = { viewModel.onProvinciaChange(it) }, label = { Text("Provincia") })
            OutlinedTextField(value = codigoPostal, onValueChange = { viewModel.onCodigoPostalChange(it) }, label = { Text("Código Postal") })
            OutlinedTextField(value = telefono, onValueChange = { viewModel.onTelefonoChange(it) }, label = { Text("Teléfono") })

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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
