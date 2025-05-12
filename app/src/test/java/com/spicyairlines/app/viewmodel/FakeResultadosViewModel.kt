// FakeResultadosViewModel.kt
package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.spicyairlines.app.model.Vuelo

class FakeResultadosViewModel : ViewModel() {

    private val _vuelosIda = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosIda: StateFlow<List<Vuelo>> = _vuelosIda.asStateFlow()

    private val _vuelosVuelta = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosVuelta: StateFlow<List<Vuelo>> = _vuelosVuelta.asStateFlow()

    private val _combinacionesValidas = MutableStateFlow<List<Pair<Vuelo, Vuelo>>>(emptyList())
    val combinacionesValidas: StateFlow<List<Pair<Vuelo, Vuelo>>> = _combinacionesValidas.asStateFlow()

    private val _cargaCompletada = MutableStateFlow(false)
    val cargaCompletada: StateFlow<Boolean> = _cargaCompletada.asStateFlow()

    /**
     * Metodo para cargar vuelos simulados.
     */
    fun cargarVuelosFake(vuelosIda: List<Vuelo>, vuelosVuelta: List<Vuelo>? = null) {
        _vuelosIda.value = vuelosIda
        _vuelosVuelta.value = vuelosVuelta ?: emptyList()
        _cargaCompletada.value = true

        if (vuelosVuelta != null) {
            _combinacionesValidas.value = generarCombinacionesValidas()
        }
    }

    /**
     * Metodo para ordenar vuelos por precio.
     */
    fun ordenarVuelos(orden: String) {
        _vuelosIda.value = if (orden == "Mayor a menor") {
            _vuelosIda.value.sortedByDescending { it.precioBase }
        } else {
            _vuelosIda.value.sortedBy { it.precioBase }
        }

        _combinacionesValidas.value = if (orden == "Mayor a menor") {
            _combinacionesValidas.value.sortedByDescending { it.first.precioBase + it.second.precioBase }
        } else {
            _combinacionesValidas.value.sortedBy { it.first.precioBase + it.second.precioBase }
        }
    }

    /**
     * Genera combinaciones válidas de vuelos de ida y vuelta.
     */
    private fun generarCombinacionesValidas(): List<Pair<Vuelo, Vuelo>> {
        val idaList = _vuelosIda.value
        val vueltaList = _vuelosVuelta.value

        return idaList.flatMap { ida ->
            vueltaList.map { vuelta -> ida to vuelta }
        }
    }
}
