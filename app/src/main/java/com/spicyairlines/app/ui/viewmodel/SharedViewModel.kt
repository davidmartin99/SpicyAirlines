package com.spicyairlines.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {

    private val _vueloSeleccionado = MutableStateFlow<Vuelo?>(null)
    val vueloSeleccionado: StateFlow<Vuelo?> = _vueloSeleccionado

    private val _vueloVueltaSeleccionado = MutableStateFlow<Vuelo?>(null)
    val vueloVueltaSeleccionado: StateFlow<Vuelo?> = _vueloVueltaSeleccionado

    private val _claseSeleccionada = MutableStateFlow("Turista")
    val claseSeleccionada: StateFlow<String> = _claseSeleccionada

    private val _precioTotal = MutableStateFlow(0.0)
    val precioTotal: StateFlow<Double> = _precioTotal

    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    private val _adultos = MutableStateFlow(0)
    val adultos: StateFlow<Int> = _adultos

    private val _ninos = MutableStateFlow(0)
    val ninos: StateFlow<Int> = _ninos

    val totalPasajeros: Int
        get() = _adultos.value + _ninos.value

    fun seleccionarVuelo(vuelo: Vuelo) {
        _vueloSeleccionado.value = vuelo
    }

    fun seleccionarVueloVuelta(vuelo: Vuelo?) {
        _vueloVueltaSeleccionado.value = vuelo
    }

    fun seleccionarClase(clase: String) {
        _claseSeleccionada.value = clase
    }

    fun actualizarPrecioTotal(precio: Double) {
        _precioTotal.value = precio
    }

    fun establecerPasajeros(lista: List<Pasajero>) {
        _pasajeros.value = lista
    }

    fun establecerAdultos(cantidad: Int) {
        _adultos.value = cantidad
    }

    fun establecerNinos(cantidad: Int) {
        _ninos.value = cantidad
    }

    fun reset() {
        _vueloSeleccionado.value = null
        _vueloVueltaSeleccionado.value = null
        _claseSeleccionada.value = ""
        _precioTotal.value = 0.0
        _pasajeros.value = emptyList()
        _adultos.value = 0
        _ninos.value = 0
    }
}
