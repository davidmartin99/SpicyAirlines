package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel para gestionar el inicio de sesión del usuario
open class LoginViewModel(
    // FirebaseAuth para manejar la autenticación
    var auth: FirebaseAuth = FirebaseAuth.getInstance(),
    // Firestore para manejar los datos del usuario
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    // Estado para manejar mensajes de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estado para manejar los datos del usuario autenticado
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    // Estado para indicar si está cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para manejar el email y contraseña ingresados
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // Función para actualizar el email ingresado
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    // Función para actualizar la contraseña ingresada
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Función para iniciar sesión con correo y contraseña
    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        _error.value = null
        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid
                if (uid != null) {
                    cargarUsuarioDesdeFirestore(uid, onSuccess)
                } else {
                    _isLoading.value = false
                    _error.value = "No se pudo obtener el ID del usuario."
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _error.value = "Correo o contraseña incorrectos."
            }
    }

    // Función privada para cargar el perfil del usuario desde Firestore
    private fun cargarUsuarioDesdeFirestore(uid: String, onSuccess: () -> Unit) {
        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val usuario = doc.toObject(Usuario::class.java)
                _usuario.value = usuario
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _isLoading.value = false
                _error.value = "Error al cargar tus datos: ${it.message}"
            }
    }
}
