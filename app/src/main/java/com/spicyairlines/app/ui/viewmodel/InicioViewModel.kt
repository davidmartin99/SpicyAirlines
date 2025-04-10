package com.spicyairlines.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp

open class InicioViewModel : ViewModel() {
    var ciudadOrigen = mutableStateOf("")
    var ciudadDestino = mutableStateOf("")
    var fechaIda = mutableStateOf<Timestamp?>(null)
    var fechaVuelta = mutableStateOf<Timestamp?>(null)
    var adultos = mutableStateOf(1)
    var ninos = mutableStateOf(0)
    var soloIda = mutableStateOf(false)
    var clase = mutableStateOf("Turista")


    fun resetCampos() {
        ciudadOrigen.value = ""
        ciudadDestino.value = ""
        fechaIda.value = null
        fechaVuelta.value = null
        adultos.value = 1
        ninos.value = 0
        clase.value = "Turista"

    }
}
