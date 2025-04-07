package com.spicyairlines.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp

class InicioViewModel : ViewModel() {
    var ciudadOrigen = mutableStateOf("")
    var ciudadDestino = mutableStateOf("")
    var fechaIda = mutableStateOf<Timestamp?>(null)
    var fechaVuelta = mutableStateOf<Timestamp?>(null)
    var adultos = mutableStateOf(0)
    var ninos = mutableStateOf(0)

    fun resetCampos() {
        ciudadOrigen.value = ""
        ciudadDestino.value = ""
        fechaIda.value = null
        fechaVuelta.value = null
        adultos.value = 0
        ninos.value = 0
    }
}
