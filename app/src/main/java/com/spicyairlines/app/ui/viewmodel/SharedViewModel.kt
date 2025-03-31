package com.spicyairlines.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.spicyairlines.app.model.Vuelo
import com.spicyairlines.app.model.Pasajero

class SharedViewModel : ViewModel() {

    var vueloSeleccionado: Vuelo? = null
    var claseSeleccionada: String = ""
    var precioTotal: Double = 0.0
    var pasajeros: List<Pasajero> = emptyList()

    var adultos: Int = 0
    var ninos: Int = 0

    val totalPasajeros: Int
        get() = adultos + ninos

    fun reset() {
        vueloSeleccionado = null
        claseSeleccionada = ""
        precioTotal = 0.0
        pasajeros = emptyList()
        adultos = 0
        ninos = 0
    }
}
