package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultadosViewModel : ViewModel() {

    var destino: String = ""
    var fechaIda: String = ""
    var fechaVuelta: String = ""

    private val _vuelos = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelos: StateFlow<List<Vuelo>> = _vuelos

    private val db = FirebaseFirestore.getInstance()

    fun cargarVuelos(destino: String, fechaIda: String, fechaVuelta: String) {
        this.destino = destino
        this.fechaIda = fechaIda
        this.fechaVuelta = fechaVuelta

        viewModelScope.launch {
            db.collection("Vuelos")
                .whereEqualTo("destino", destino)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.documents.mapNotNull { doc ->
                        val vuelo = doc.toObject(Vuelo::class.java)
                        vuelo?.copy(id = doc.id)
                    }.filter {
                        it.fechaSalida >= fechaIda && it.fechaVuelta <= fechaVuelta
                    }
                    _vuelos.value = lista
                }
        }
    }
}
