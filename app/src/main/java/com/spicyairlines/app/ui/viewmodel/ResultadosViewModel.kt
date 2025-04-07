package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResultadosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _vuelosIda = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosIda: StateFlow<List<Vuelo>> = _vuelosIda

    private val _vuelosVuelta = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosVuelta: StateFlow<List<Vuelo>> = _vuelosVuelta

    fun cargarVuelos(destino: String, fechaIda: Timestamp?, fechaVuelta: Timestamp?) {
        viewModelScope.launch {
            try {
                val vuelos = db.collection("Vuelos")
                    .whereEqualTo("destino", destino)
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Vuelo::class.java)?.copy(id = doc.id)
                    }

                // Vuelos de ida
                _vuelosIda.value = vuelos.filter {
                    it.fechaSalida >= (fechaIda ?: Timestamp.now()) // Asegúrate de que la fecha de ida se compara como Timestamp
                }

                // Vuelos de vuelta (solo si hay fecha)
                if (fechaVuelta != null) {
                    _vuelosVuelta.value = vuelos.filter {
                        it.origen == destino &&
                                it.fechaSalida >= fechaVuelta // Se compara la fecha de vuelta también como Timestamp
                    }
                } else {
                    _vuelosVuelta.value = emptyList()
                }

            } catch (e: Exception) {
                _vuelosIda.value = emptyList()
                _vuelosVuelta.value = emptyList()
                println("Error al cargar vuelos: ${e.message}")
            }
        }
    }

}
