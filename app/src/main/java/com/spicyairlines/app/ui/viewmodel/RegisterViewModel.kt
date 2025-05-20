package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.spicyairlines.app.ui.utils.NivelSeguridadContrasena

// ViewModel para gestionar el registro de nuevos usuarios
class RegisterViewModel : ViewModel() {

    // Instancias de Firebase para autenticación y base de datos
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Campos del formulario
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _nivelContrasena = MutableStateFlow(NivelSeguridadContrasena.MUY_DEBIL)
    val nivelContrasena: StateFlow<NivelSeguridadContrasena> = _nivelContrasena

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

    // Errores individuales
    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre

    private val _errorApellidos = MutableStateFlow<String?>(null)
    val errorApellidos: StateFlow<String?> = _errorApellidos

    private val _errorCiudad = MutableStateFlow<String?>(null)
    val errorCiudad: StateFlow<String?> = _errorCiudad

    private val _errorProvincia = MutableStateFlow<String?>(null)
    val errorProvincia: StateFlow<String?> = _errorProvincia

    private val _errorCodigoPostal = MutableStateFlow<String?>(null)
    val errorCodigoPostal: StateFlow<String?> = _errorCodigoPostal

    private val _errorTelefono = MutableStateFlow<String?>(null)
    val errorTelefono: StateFlow<String?> = _errorTelefono

    private val _errorPassword = MutableStateFlow<String?>(null)
    val errorPassword: StateFlow<String?> = _errorPassword

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Funciones de actualización y validación
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _error.value = if (isValidEmail(newEmail)) null else "Formato de correo inválido"
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validarPassword()
    }

    fun onNombreChange(newNombre: String) {
        _nombre.value = newNombre
        _errorNombre.value = if (newNombre.matches(Regex("^[\\p{L}\\s'-]{2,50}$"))) null
        else "Nombre inválido"
    }

    fun onApellidosChange(newApellidos: String) {
        _apellidos.value = newApellidos
        _errorApellidos.value = if (newApellidos.matches(Regex("^[\\p{L}\\s'-]{2,50}$"))) null
        else "Apellidos inválidos"
    }

    fun onCiudadChange(newCiudad: String) {
        _ciudad.value = newCiudad
        _errorCiudad.value = if (newCiudad.matches(Regex("^[\\p{L}\\s'-]{2,50}$"))) null
        else "Ciudad inválida"
    }

    fun onProvinciaChange(newProvincia: String) {
        _provincia.value = newProvincia
        _errorProvincia.value = if (newProvincia.matches(Regex("^[\\p{L}\\s'-]{2,50}$"))) null
        else "Provincia inválida"
    }

    fun onCodigoPostalChange(newCodigoPostal: String) {
        _codigoPostal.value = newCodigoPostal
        _errorCodigoPostal.value = if (newCodigoPostal.length == 5 && newCodigoPostal.all { it.isDigit() }) null
        else "Código postal inválido"
    }

    fun onTelefonoChange(newTelefono: String) {
        _telefono.value = newTelefono
        _errorTelefono.value = if (newTelefono.length == 9 && newTelefono.all { it.isDigit() }) null
        else "Teléfono inválido"
    }

    // Función para verificar el formato del email
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return email.matches(emailRegex)
    }
    // Función para validar la seguridad de la contraseña
    fun validarPassword() {
        val password = _password.value

        val tieneMayusculas = password.count { it.isUpperCase() }
        val tieneMinusculas = password.any { it.isLowerCase() }
        val tieneNumeros = password.count { it.isDigit() }
        val tieneSimbolos = password.any { !it.isLetterOrDigit() }
        val longitud = password.length

        val cumpleRequisitosBasicos = longitud >= 8 && tieneMayusculas >= 1 && tieneMinusculas && tieneNumeros >= 1 && tieneSimbolos

        _nivelContrasena.value = when {
            password.isBlank() -> NivelSeguridadContrasena.MUY_DEBIL
            !cumpleRequisitosBasicos -> NivelSeguridadContrasena.MUY_DEBIL
            longitud in 8..10 -> NivelSeguridadContrasena.DEBIL
            longitud in 11..12 && tieneMayusculas > 1 && tieneNumeros > 1 -> NivelSeguridadContrasena.NORMAL
            longitud in 13..14 && tieneMayusculas > 1 && tieneNumeros > 1 -> NivelSeguridadContrasena.BUENA
            longitud > 14 && tieneMayusculas > 1 && tieneNumeros > 1 -> NivelSeguridadContrasena.EXCELENTE
            else -> NivelSeguridadContrasena.DEBIL
        }

        _errorPassword.value = when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            !cumpleRequisitosBasicos -> "Debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo"
            else -> null
        }
    }

    // Registro con Firebase
    fun register(onSuccess: () -> Unit) {
        _error.value = null

        // Revisión rápida de errores individuales antes de registrar
        if (listOf(
                _errorNombre.value,
                _errorApellidos.value,
                _errorCiudad.value,
                _errorProvincia.value,
                _errorCodigoPostal.value,
                _errorTelefono.value,
                _errorPassword.value
            ).any { it != null }
        ) {
            _error.value = "Corrige los errores antes de continuar"
            return
        }

        // Intento de registro en Firebase Authentication
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

                    // Guardar usuario en Firestore
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
