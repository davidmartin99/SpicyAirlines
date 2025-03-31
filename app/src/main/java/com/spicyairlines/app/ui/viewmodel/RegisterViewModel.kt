package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(
        nickname: String,
        email: String,
        password: String,
        nombre: String,
        apellidos: String,
        ciudad: String,
        provincia: String,
        codigoPostal: String,
        telefono: String,
        onSuccess: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userData = mapOf(
                    "id" to nickname,
                    "email" to email,
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "ciudad" to ciudad,
                    "provincia" to provincia,
                    "codigoPostal" to codigoPostal,
                    "telefono" to telefono
                )

                db.collection("usuarios").document(uid).set(userData)
                    .addOnSuccessListener {
                        _error.value = null
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Error al guardar usuario: ${e.message}"
                    }
            }
            .addOnFailureListener { e ->
                _error.value = "Error al registrar: ${e.message}"
            }
    }
}
