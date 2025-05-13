package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Nos permite simular el comportamiento del proceso de inicio de sesión sin conectarse a un backend real.
class FakeLoginViewModel : ViewModel() {

    // MutableStateFlow para almacenar el correo electrónico ingresado por el usuario.
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    // MutableStateFlow para almacenar la contraseña ingresada por el usuario.
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // MutableStateFlow para manejar los mensajes de error (por ejemplo, credenciales incorrectas).
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // MutableStateFlow para indicar si el proceso de inicio de sesión está en curso.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Metodo para actualizar el correo electrónico ingresado por el usuario.
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    // Metodo para actualizar la contraseña ingresada por el usuario.
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Metodo de inicio de sesión simulado.
    // Si las credenciales coinciden con las de prueba, se limpia el error.
    // Si no coinciden, se establece un mensaje de error.
    fun login(email: String, password: String) {
        if (email == "test@example.com" && password == "password123") {
            _error.value = null  // Inicio de sesión exitoso, sin errores.
        } else {
            _error.value = "Correo o contraseña incorrectos."  // Error de inicio de sesión.
        }
    }
}
