package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid
                if (uid != null) {
                    cargarUsuarioDesdeFirestore(uid, onSuccess)
                } else {
                    _error.value = "No se pudo obtener el UID."
                }
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    private fun cargarUsuarioDesdeFirestore(uid: String, onSuccess: () -> Unit) {
        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val usuario = doc.toObject(Usuario::class.java)
                _usuario.value = usuario
                onSuccess()
            }
            .addOnFailureListener {
                _error.value = "Error al cargar datos del usuario: ${it.message}"
            }
    }
}
