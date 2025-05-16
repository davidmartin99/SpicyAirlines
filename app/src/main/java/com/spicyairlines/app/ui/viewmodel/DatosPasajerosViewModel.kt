package com.spicyairlines.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.spicyairlines.app.model.Pasajero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

// ViewModel para gestionar la información de los pasajeros
class DatosPasajerosViewModel : ViewModel() {

    // Flow para manejar la lista de pasajeros
    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    // Flow para manejar los errores de validación
    private val _errores = MutableStateFlow<List<String?>>(emptyList())
    val errores: StateFlow<List<String?>> = _errores

    // Variables para contar adultos y niños esperados
    private var adultosEsperados = 0
    private var ninosEsperados = 0

    // Inicializa los formularios de pasajeros según la cantidad esperada
    fun inicializarFormularios(numPasajeros: Int, adultos: Int, ninos: Int) {
        if (_pasajeros.value.isEmpty()) {
            _pasajeros.value = List(numPasajeros) { Pasajero() }
            _errores.value = List(numPasajeros) { null }
            adultosEsperados = adultos
            ninosEsperados = ninos
        }
    }

    // Actualiza un campo específico de un pasajero
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
    }

    // Actualiza la fecha de nacimiento de un pasajero
    fun actualizarFechaNacimiento(index: Int, fecha: Date) {
        val lista = _pasajeros.value.toMutableList()
        if (index in lista.indices) {
            lista[index] = lista[index].copy(fechaNacimiento = Timestamp(fecha))
            _pasajeros.value = lista
        }
    }

    // Valida todos los pasajeros y verifica sus datos
    fun validarTodosLosPasajeros(): Boolean {
        val erroresList = _pasajeros.value.mapIndexed { index, pasajero ->
            when {
                pasajero.nombre.isBlank() -> "Error: Nombre no válido."
                pasajero.apellidos.isBlank() -> "Error: Apellidos no válidos."
                !Regex("^[A-Z]{3}\\d{6}\$").matches(pasajero.numeroPasaporte) -> "Error: Pasaporte debe tener 3 letras y 6 números."
                pasajero.telefono.length != 9 || !pasajero.telefono.all { it.isDigit() } -> "Error: Teléfono debe tener 9 dígitos."
                calcularEdad(pasajero) < 3 && index < adultosEsperados -> "Error: Edad inválida para un adulto."
                calcularEdad(pasajero) >= 3 && index >= adultosEsperados -> "Error: Edad inválida para un niño."
                else -> null
            }
        }

        _errores.value = erroresList
        return erroresList.all { it == null }
    }

    // Calcula la edad de un pasajero a partir de su fecha de nacimiento
    private fun calcularEdad(pasajero: Pasajero): Int {
        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply {
            time = pasajero.fechaNacimiento.toDate()
        }
        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--
        return edad
    }
}
