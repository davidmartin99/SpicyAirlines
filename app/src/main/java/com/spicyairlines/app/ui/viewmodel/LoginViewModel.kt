package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
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
                val mensaje = when {
                    it.message?.contains("password") == true -> "Contraseña incorrecta."
                    it.message?.contains("no user record") == true -> "Correo no registrado."
                    else -> "Error al iniciar sesión: ${it.message}"
                }
                _error.value = mensaje
            }
    }

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
