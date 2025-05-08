package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.utils.validarPasajero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class DatosPasajerosViewModel : ViewModel() {

    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    private val _errores = MutableStateFlow<List<String?>>(emptyList())
    val errores: StateFlow<List<String?>> = _errores

    private val db = FirebaseFirestore.getInstance()

    fun inicializarFormularios(numPasajeros: Int) {
        if (_pasajeros.value.isEmpty()) {
            _pasajeros.value = List(numPasajeros) { Pasajero() }
            _errores.value = List(numPasajeros) { null }
        }
    }

    fun actualizarCampo(index: Int, campo: String, valor: String) {
        val lista = _pasajeros.value.toMutableList()
        if (index !in lista.indices) return

        val pasajero = lista[index]
        lista[index] = when (campo) {
            "nombre" -> pasajero.copy(nombre = valor)
            "apellidos" -> pasajero.copy(apellidos = valor)
            "numeroPasaporte" -> pasajero.copy(numeroPasaporte = valor)
            "telefono" -> pasajero.copy(telefono = valor)
            else -> pasajero
        }

        _pasajeros.value = lista
        validarPasajeroYActualizarError(index)
    }

    fun actualizarFechaNacimiento(index: Int, fecha: Date) {
        val lista = _pasajeros.value.toMutableList()
        if (index in lista.indices) {
            lista[index] = lista[index].copy(fechaNacimiento = Timestamp(fecha))
            _pasajeros.value = lista
            validarPasajeroYActualizarError(index)
        }
    }

    private fun validarPasajeroYActualizarError(index: Int) {
        val pasajero = _pasajeros.value.getOrNull(index) ?: return
        val resultado = validarPasajero(pasajero)

        _errores.update { errores ->
            errores.toMutableList().apply {
                this[index] = if (resultado.esValido) null else resultado.mensajeError
            }
        }
    }

    fun validarTodosLosPasajeros(): Boolean {
        _pasajeros.value.forEachIndexed { index, _ ->
            validarPasajeroYActualizarError(index)
        }
        return _errores.value.all { it == null }
    }
}
