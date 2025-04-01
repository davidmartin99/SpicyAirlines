package com.spicyairlines.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    val error by viewModel.error.collectAsState()

    BasePantalla(title = "Registro") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") })
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") })
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") })
            OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") })
            OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") })
            OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") })
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })

            Button(onClick = {
                viewModel.register(
                    email = email,
                    password = password,
                    nombre = nombre,
                    apellidos = apellidos,
                    ciudad = ciudad,
                    provincia = provincia,
                    codigoPostal = codigoPostal,
                    telefono = telefono,
                    onSuccess = onRegisterSuccess
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrarse")
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
