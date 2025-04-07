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
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Vuelo::class.java)?.copy(id = doc.id)
                    }

                // Vuelos de ida: origen = Madrid, destino = seleccionado
                _vuelosIda.value = vuelos.filter {
                    it.origen == "Madrid" &&
                            it.destino == destino &&
                            fechaIda != null &&
                            it.fechaSalida >= fechaIda
                }

                // Vuelos de vuelta: origen = destino, destino = Madrid
                _vuelosVuelta.value = if (fechaVuelta != null) {
                    vuelos.filter {
                        it.origen == destino &&
                                it.destino == "Madrid" &&
                                it.fechaSalida >= fechaVuelta
                    }
                } else {
                    emptyList()
                }

            } catch (e: Exception) {
                _vuelosIda.value = emptyList()
                _vuelosVuelta.value = emptyList()
                println("Error al cargar vuelos: ${e.message}")
            }
        }
    }
}
