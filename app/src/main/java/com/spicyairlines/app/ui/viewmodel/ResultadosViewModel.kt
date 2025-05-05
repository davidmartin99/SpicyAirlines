package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.spicyairlines.app.ui.utils.plusDays

class ResultadosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _vuelosIda = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosIda: StateFlow<List<Vuelo>> = _vuelosIda.asStateFlow()

    private val _vuelosVuelta = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosVuelta: StateFlow<List<Vuelo>> = _vuelosVuelta.asStateFlow()

    private val _combinacionesValidas = MutableStateFlow<List<Pair<Vuelo, Vuelo>>>(emptyList())
    val combinacionesValidas: StateFlow<List<Pair<Vuelo, Vuelo>>> = _combinacionesValidas.asStateFlow()

    private val _cargaCompletada = MutableStateFlow(false)
    val cargaCompletada: StateFlow<Boolean> = _cargaCompletada.asStateFlow()

    fun cargarVuelos(
        origen: String,
        destino: String,
        fechaIda: Timestamp,
        fechaVuelta: Timestamp?,
        claseSeleccionada: String,
        totalPasajeros: Int
    ) {
        val fechaLimiteIda = fechaVuelta ?: fechaIda.plusDays(1)
        val fechaMinimaVuelta = fechaIda.plusDays(2)

        viewModelScope.launch {
            _cargaCompletada.value = false

            db.collection("Vuelos")
                .whereEqualTo("origen", origen)
                .whereEqualTo("destino", destino)
                .whereGreaterThanOrEqualTo("fechaSalida", fechaIda)
                .whereLessThanOrEqualTo("fechaSalida", fechaLimiteIda)
                .get()
                .addOnSuccessListener { result ->
                    val vuelosIdaList = result.documents.mapNotNull { doc ->
                        doc.toObject(Vuelo::class.java)?.copy(id = doc.id)
                    }.filter {
                        vueloTieneAsientos(it, claseSeleccionada, totalPasajeros)
                    }

                    _vuelosIda.value = vuelosIdaList

                    if (fechaVuelta != null) {
                        db.collection("Vuelos")
                            .whereEqualTo("origen", destino)
                            .whereEqualTo("destino", origen)
                            .whereGreaterThanOrEqualTo("fechaSalida", fechaMinimaVuelta)
                            .whereLessThanOrEqualTo("fechaSalida", fechaVuelta)
                            .get()
                            .addOnSuccessListener { vueltaResult ->
                                val vuelosVueltaList = vueltaResult.documents.mapNotNull { doc ->
                                    doc.toObject(Vuelo::class.java)?.copy(id = doc.id)
                                }.filter {
                                    vueloTieneAsientos(it, claseSeleccionada, totalPasajeros)
                                }

                                _vuelosVuelta.value = vuelosVueltaList

                                val combinaciones = generarCombinacionesValidas(claseSeleccionada, totalPasajeros)

                                if (combinaciones.isEmpty()) {
                                    _vuelosIda.value = emptyList()
                                    _vuelosVuelta.value = emptyList()
                                    _combinacionesValidas.value = emptyList()
                                } else {
                                    _combinacionesValidas.value = combinaciones
                                }

                                _cargaCompletada.value = true
                            }
                            .addOnFailureListener {
                                _cargaCompletada.value = true
                            }
                    } else {
                        _vuelosVuelta.value = emptyList()
                        _combinacionesValidas.value = emptyList()
                        _cargaCompletada.value = true
                    }
                }
                .addOnFailureListener {
                    _cargaCompletada.value = true
                }
        }
    }

    private fun generarCombinacionesValidas(clase: String, pasajeros: Int): List<Pair<Vuelo, Vuelo>> {
        val idaList = _vuelosIda.value
        val vueltaList = _vuelosVuelta.value

        return idaList.flatMap { ida ->
            vueltaList.filter { vuelta ->
                vuelta.fechaSalida.toDate().after(ida.fechaLlegada.toDate()) &&
                        vueloTieneAsientos(ida, clase, pasajeros) &&
                        vueloTieneAsientos(vuelta, clase, pasajeros)
            }.map { vuelta -> ida to vuelta }
        }
    }

    private fun vueloTieneAsientos(vuelo: Vuelo, clase: String, pasajeros: Int): Boolean {
        return when (clase) {
            "Turista" -> vuelo.asientosTurista >= pasajeros
            "Premium" -> vuelo.asientosPremium >= pasajeros
            "Business" -> vuelo.asientosBusiness >= pasajeros
            else -> false
        }
    }
}
