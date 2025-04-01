package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(
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
        _error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    val usuario = Usuario(
                        email = email,
                        nombre = nombre,
                        apellidos = apellidos,
                        ciudad = ciudad,
                        provincia = provincia,
                        codigoPostal = codigoPostal,
                        telefono = telefono
                    )

                    firestore.collection("usuarios")
                        .document(uid)
                        .set(usuario)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            _error.value = "Error al guardar datos: ${it.message}"
                        }
                } else {
                    _error.value = "No se pudo obtener el UID del usuario"
                }
            }
            .addOnFailureListener {
                _error.value = "Error de autenticación: ${it.message}"
            }
    }
}
