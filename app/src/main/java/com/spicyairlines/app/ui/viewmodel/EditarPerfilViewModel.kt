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

class EditarPerfilViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val uid = auth.currentUser?.uid

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    var nuevoPassword: String = ""

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
