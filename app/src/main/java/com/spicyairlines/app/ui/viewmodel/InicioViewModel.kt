package com.spicyairlines.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class InicioViewModel : ViewModel() {
    var ciudadDestino = mutableStateOf("")
    var fechaIda = mutableStateOf("")
    var fechaVuelta = mutableStateOf("")
    var adultos = mutableStateOf(0)
    var ninos = mutableStateOf(0)

    fun resetCampos() {
        ciudadDestino.value = ""
        fechaIda.value = ""
        fechaVuelta.value = ""
        adultos.value = 0
        ninos.value = 0
    }
}
