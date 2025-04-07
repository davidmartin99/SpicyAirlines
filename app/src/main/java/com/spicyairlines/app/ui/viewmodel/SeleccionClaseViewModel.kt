package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeleccionClaseViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _disponibilidad = MutableStateFlow<Map<String, Int>>(emptyMap())
    val disponibilidad: StateFlow<Map<String, Int>> = _disponibilidad

    fun consultarAsientosDisponibles(vueloId: String) {
        viewModelScope.launch {
            db.collection("Vuelos").document(vueloId).get()
                .addOnSuccessListener { doc ->
                    val mapa = mapOf(
                        "Turista" to (doc.getLong("asientosTurista") ?: 0L).toInt(),
                        "Premium" to (doc.getLong("asientosPremium") ?: 0L).toInt(),
                        "Business" to (doc.getLong("asientosBusiness") ?: 0L).toInt()
                    )
                    _disponibilidad.value = mapa
                }
                .addOnFailureListener {
                    _disponibilidad.value = emptyMap() // por si falla la carga
                }
        }
    }
}
