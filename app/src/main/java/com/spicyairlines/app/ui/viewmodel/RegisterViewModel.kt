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

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _apellidos = MutableStateFlow("")
    val apellidos: StateFlow<String> = _apellidos

    private val _ciudad = MutableStateFlow("")
    val ciudad: StateFlow<String> = _ciudad

    private val _provincia = MutableStateFlow("")
    val provincia: StateFlow<String> = _provincia

    private val _codigoPostal = MutableStateFlow("")
    val codigoPostal: StateFlow<String> = _codigoPostal

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }
    fun onNombreChange(newNombre: String) { _nombre.value = newNombre }
    fun onApellidosChange(newApellidos: String) { _apellidos.value = newApellidos }
    fun onCiudadChange(newCiudad: String) { _ciudad.value = newCiudad }
    fun onProvinciaChange(newProvincia: String) { _provincia.value = newProvincia }
    fun onCodigoPostalChange(newCodigoPostal: String) { _codigoPostal.value = newCodigoPostal }
    fun onTelefonoChange(newTelefono: String) { _telefono.value = newTelefono }

    fun register(onSuccess: () -> Unit) {
        _error.value = null

        auth.createUserWithEmailAndPassword(_email.value, _password.value)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    val usuario = Usuario(
                        email = _email.value,
                        nombre = _nombre.value,
                        apellidos = _apellidos.value,
                        ciudad = _ciudad.value,
                        provincia = _provincia.value,
                        codigoPostal = _codigoPostal.value,
                        telefono = _telefono.value
                    )

                    firestore.collection("usuarios")
                        .document(uid)
                        .set(usuario)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener {
                            _error.value = "Error al guardar tus datos: ${it.message}"
                        }
                } else {
                    _error.value = "No se pudo obtener el ID del usuario"
                }
            }
            .addOnFailureListener {
                val mensaje = when {
                    it.message?.contains("email address is already in use") == true -> "Este correo ya está registrado."
                    it.message?.contains("badly formatted") == true -> "Formato de correo inválido."
                    it.message?.contains("Password should be at least") == true -> "La contraseña debe tener al menos 6 caracteres."
                    else -> "Error al registrarse: ${it.message}"
                }
                _error.value = mensaje
            }
    }

    fun setError(mensaje: String) {
        _error.value = mensaje
    }
}
