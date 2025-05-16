package com.spicyairlines.app.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para gestionar la edición del perfil del usuario
class EditarPerfilViewModel : ViewModel() {
    // Instancia de FirebaseAuth para autenticación
    private val auth = FirebaseAuth.getInstance()
    // Instancia de Firebase Firestore para base de datos
    private val db = FirebaseFirestore.getInstance()
    // ID del usuario autenticado
    private val uid = auth.currentUser?.uid

    // Estado del usuario (perfil)
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    // Estado de carga (true mientras se carga el perfil)
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    // Variable para manejar la nueva contraseña (si se desea cambiar)
    var nuevoPassword: String = ""

    // Función para cargar el perfil del usuario desde Firestore
    fun cargarUsuario() {
        uid?.let {
            viewModelScope.launch {
                db.collection("usuarios").document(it).get()
                    .addOnSuccessListener { doc ->
                        _usuario.value = doc.toObject(Usuario::class.java)
                        _loading.value = false
                    }
            }
        }
    }

    // Función para guardar los cambios en el perfil del usuario
    fun guardarCambios(
        nombre: String,
        apellidos: String,
        ciudad: String,
        provincia: String,
        codigoPostal: String,
        telefono: String,
        contexto: Context
    ) {
        uid?.let {
            // Actualiza los datos del usuario en Firestore
            db.collection("usuarios").document(it).update(
                mapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "ciudad" to ciudad,
                    "provincia" to provincia,
                    "codigoPostal" to codigoPostal,
                    "telefono" to telefono
                )
            ).addOnSuccessListener {
                Toast.makeText(contexto, "Perfil guardado", Toast.LENGTH_SHORT).show()
            }

            // Si el usuario ha ingresado una nueva contraseña
            if (nuevoPassword.isNotBlank()) {
                auth.currentUser?.updatePassword(nuevoPassword)
                    ?.addOnSuccessListener {
                        Toast.makeText(contexto, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(contexto, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
