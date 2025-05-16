package com.spicyairlines.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Pasajero
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*

// ViewModel para gestionar la edición de pasajeros en una reserva
class EditarPasajerosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Estado para manejar la lista de pasajeros
    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros

    // Estado para manejar la carga de datos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para indicar si los cambios fueron guardados correctamente
    private val _cambiosGuardados = MutableStateFlow(false)
    val cambiosGuardados: StateFlow<Boolean> = _cambiosGuardados

    // Estado para manejar los vuelos asociados a la reserva
    private val _vuelos = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelos: StateFlow<List<Vuelo>> = _vuelos

    // Carga los pasajeros de la reserva especificada
    fun cargarPasajeros(reservaId: String) {
        _isLoading.value = true
        db.collection("reservas").document(reservaId).collection("pasajeros")
            .get()
            .addOnSuccessListener { documents ->
                val listaPasajeros = documents.mapNotNull { doc ->
                    doc.toObject(Pasajero::class.java).copy(id = doc.id) // 👈 muy importante
                }
                _pasajeros.value = listaPasajeros
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    // Carga los vuelos asociados a la reserva
    fun cargarVuelosDeReserva(reservaId: String) {
        db.collection("reservas").document(reservaId)
            .get()
            .addOnSuccessListener { reservaDoc ->
                val vuelosIds = reservaDoc.get("vuelos") as? List<String> ?: emptyList()
                val vuelosTemp = mutableListOf<Vuelo>()

                vuelosIds.forEach { vueloId ->
                    db.collection("Vuelos").document(vueloId)
                        .get()
                        .addOnSuccessListener { vueloDoc ->
                            vueloDoc.toObject(Vuelo::class.java)?.let { vuelo ->
                                vuelosTemp.add(vuelo)
                                _vuelos.value = vuelosTemp.sortedBy { it.fechaSalida }
                            }
                        }
                }
            }
    }

    // Métodos para actualizar campos específicos de un pasajero
    fun actualizarNombre(index: Int, nuevoNombre: String) {
        _pasajeros.update { lista ->
            lista.toMutableList().also { it[index] = it[index].copy(nombre = nuevoNombre) }
        }
    }

    fun actualizarApellidos(index: Int, nuevosApellidos: String) {
        _pasajeros.update { lista ->
            lista.toMutableList().also { it[index] = it[index].copy(apellidos = nuevosApellidos) }
        }
    }

    fun actualizarPasaporte(index: Int, nuevoPasaporte: String) {
        _pasajeros.update { lista ->
            lista.toMutableList().also { it[index] = it[index].copy(numeroPasaporte = nuevoPasaporte) }
        }
    }

    fun actualizarTelefono(index: Int, nuevoTelefono: String) {
        _pasajeros.update { lista ->
            lista.toMutableList().also { it[index] = it[index].copy(telefono = nuevoTelefono) }
        }
    }

    fun actualizarFechaNacimiento(index: Int, nuevaFecha: Date) {
        _pasajeros.update { lista ->
            lista.toMutableList().also {
                it[index] = it[index].copy(fechaNacimiento = com.google.firebase.Timestamp(nuevaFecha))
            }
        }
    }

    // Guarda los cambios realizados a los pasajeros en Firebase
    fun guardarCambios(reservaId: String) {
        _isLoading.value = true
        val batch = db.batch()
        val pasajerosRef = db.collection("reservas").document(reservaId).collection("pasajeros")

        pasajeros.value.forEach { pasajero ->
            val docRef = pasajerosRef.document(pasajero.id) // 👈 usamos el id real del pasajero
            batch.set(docRef, pasajero)
        }

        batch.commit()
            .addOnSuccessListener {
                _isLoading.value = false
                _cambiosGuardados.value = true
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

}
