package com.spicyairlines.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel compartido para manejar datos comunes entre pantallas
class SharedViewModel : ViewModel() {

    // Estado para manejar el vuelo de ida seleccionado
    private val _vueloSeleccionado = MutableStateFlow<Vuelo?>(null)
    val vueloSeleccionado: StateFlow<Vuelo?> = _vueloSeleccionado

    // Estado para manejar el vuelo de vuelta seleccionado
    private val _vueloVueltaSeleccionado = MutableStateFlow<Vuelo?>(null)
    val vueloVueltaSeleccionado: StateFlow<Vuelo?> = _vueloVueltaSeleccionado

    // Estado para manejar la clase seleccionada (Turista, Premium, Business)
    private val _claseSeleccionada = MutableStateFlow("Turista")
    val claseSeleccionada: StateFlow<String> = _claseSeleccionada

    // Estado para manejar el precio total del billete
    private val _precioTotal = MutableStateFlow(0.0)
    val precioTotal: StateFlow<Double> = _precioTotal

    // Estado para manejar la lista de pasajeros
    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    // Estado para manejar el número de adultos
    private val _adultos = MutableStateFlow(0)
    val adultos: StateFlow<Int> = _adultos

    // Estado para manejar el número de niños
    private val _ninos = MutableStateFlow(0)
    val ninos: StateFlow<Int> = _ninos

    // Calcula el total de pasajeros (adultos + niños)
    val totalPasajeros: Int
        get() = _adultos.value + _ninos.value

    // Estado para manejar el precio total de la reserva
    private val _precioTotalReserva = MutableStateFlow(0.0)
    val precioTotalReserva: StateFlow<Double> = _precioTotalReserva

    /**
     * Métodos para actualizar el estado
     */

    // Seleccionar vuelo de ida
    fun seleccionarVuelo(vuelo: Vuelo) {
        _vueloSeleccionado.value = vuelo
    }

    // Seleccionar vuelo de vuelta
    fun seleccionarVueloVuelta(vuelo: Vuelo?) {
        _vueloVueltaSeleccionado.value = vuelo
    }

    // Seleccionar clase de vuelo
    fun seleccionarClase(clase: String) {
        _claseSeleccionada.value = clase
    }

    // Establecer lista de pasajeros
    fun establecerPasajeros(lista: List<Pasajero>) {
        _pasajeros.value = lista
    }

    // Establecer número de adultos
    fun establecerAdultos(cantidad: Int) {
        _adultos.value = cantidad
    }

    // Establecer número de niños
    fun establecerNinos(cantidad: Int) {
        _ninos.value = cantidad
    }

    // Calcular el precio por billete según la clase seleccionada
    fun calcularPrecioBillete(vueloIda: Vuelo, vueloVuelta: Vuelo? = null) {
        val clase = _claseSeleccionada.value
        val multiplicador = when (clase) {
            "Premium" -> 1.5
            "Business" -> 2.0
            else -> 1.0
        }

        val precioBase = vueloIda.precioBase + (vueloVuelta?.precioBase ?: 0)
        val precioUnitario = precioBase * multiplicador

        _precioTotal.value = precioUnitario
    }

    // Actualizar el precio total de la reserva
    fun actualizarPrecioTotalReserva(precio: Double) {
        _precioTotalReserva.value = precio
    }

}
