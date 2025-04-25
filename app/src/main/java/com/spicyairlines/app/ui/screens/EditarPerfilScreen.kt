package com.spicyairlines.app.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.spicyairlines.app.components.BasePantalla
import com.spicyairlines.app.utils.validarCamposUsuario
import com.spicyairlines.app.viewmodel.EditarPerfilViewModel

@Composable
fun EditarPerfilScreen(
    viewModel: EditarPerfilViewModel,
    onBack: () -> Unit
) {
    val contexto = LocalContext.current
    val usuario by viewModel.usuario.collectAsState()
    val cargando by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUsuario()
    }

    BasePantalla(onBack = onBack) { padding ->

        if (cargando || usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val user = usuario!!

            var nombre by remember { mutableStateOf(user.nombre) }
            var apellidos by remember { mutableStateOf(user.apellidos) }
            var ciudad by remember { mutableStateOf(user.ciudad) }
            var provincia by remember { mutableStateOf(user.provincia) }
            var codigoPostal by remember { mutableStateOf(user.codigoPostal) }
            var telefono by remember { mutableStateOf(user.telefono) }
            var nuevoPassword by remember { mutableStateOf("") }
            var errorMensaje by remember { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Editar perfil", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(value = user.email, onValueChange = {}, label = { Text("Correo (no editable)") }, enabled = false)
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") })
                OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") })
                OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") })
                OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") })
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })

                OutlinedTextField(
                    value = nuevoPassword,
                    onValueChange = {
                        nuevoPassword = it
                        viewModel.nuevoPassword = it
                    },
                    label = { Text("Nueva contraseña (opcional)") },
                    visualTransformation = PasswordVisualTransformation()
                )

                if (errorMensaje != null) {
                    Text(text = errorMensaje!!, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        val resultado = validarCamposUsuario(
                            nombre, apellidos, ciudad, provincia, codigoPostal, telefono,
                            password = if (nuevoPassword.isNotBlank()) nuevoPassword else null
                        )

                        if (!resultado.esValido) {
                            errorMensaje = resultado.mensajeError
                            return@Button
                        }

                        errorMensaje = null
                        viewModel.guardarCambios(
                            nombre = nombre,
                            apellidos = apellidos,
                            ciudad = ciudad,
                            provincia = provincia,
                            codigoPostal = codigoPostal,
                            telefono = telefono,
                            contexto = contexto
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    }
}

