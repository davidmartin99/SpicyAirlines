package com.spicyairlines.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp

// ViewModel para manejar los datos de búsqueda de vuelos en la pantalla principal
open class PrincipalViewModel : ViewModel() {
    var ciudadOrigen = mutableStateOf("")
    var ciudadDestino = mutableStateOf("")
    var fechaIda = mutableStateOf<Timestamp?>(null)
    var fechaVuelta = mutableStateOf<Timestamp?>(null)
    var adultos = mutableStateOf(1)
    var ninos = mutableStateOf(0)
    var soloIda = mutableStateOf(false)
    var clase = mutableStateOf("Turista")
}
