package com.spicyairlines.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import com.spicyairlines.app.ui.utils.plusDays

class ResultadosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _vuelosIda = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosIda: StateFlow<List<Vuelo>> = _vuelosIda.asStateFlow()

    private val _vuelosVuelta = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelosVuelta: StateFlow<List<Vuelo>> = _vuelosVuelta.asStateFlow()

    private val _combinacionesValidas = MutableStateFlow<List<Pair<Vuelo, Vuelo>>>(emptyList())
    val combinacionesValidas: StateFlow<List<Pair<Vuelo, Vuelo>>> = _combinacionesValidas.asStateFlow()

    /**
     * Carga vuelos desde Firebase según los filtros de búsqueda.
     * Si se proporciona una fecha de vuelta, también busca vuelos de regreso.
     *
     * @param origen Ciudad de origen (ej: "Madrid")
     * @param destino Ciudad de destino (ej: "Chongqing")
     * @param fechaIda Fecha mínima de salida del vuelo de ida
     * @param fechaVuelta Fecha mínima de salida del vuelo de vuelta (opcional)
     */
    fun cargarVuelos(
        origen: String,
        destino: String,
        fechaIda: Timestamp?,
        fechaVuelta: Timestamp?
    ) {
        if (fechaIda == null) {
            Log.e("ResultadosViewModel12", "❌ fechaIda es null")
            return
        }

        val fechaLimiteIda = fechaVuelta ?: fechaIda
        val fechaMinimaVuelta = fechaIda.plusDays(2)

        Log.d("ResultadosViewModel13", "🛫 Buscando vuelos: $origen → $destino entre ${fechaIda.toDate()} y ${fechaLimiteIda.toDate()}")

        viewModelScope.launch {
            db.collection("Vuelos")
                .whereEqualTo("origen", origen)
                .whereEqualTo("destino", destino)
                .whereGreaterThanOrEqualTo("fechaSalida", fechaIda)
                .whereLessThanOrEqualTo("fechaSalida", fechaLimiteIda)
                .get()
                .addOnSuccessListener { result ->
                    val vuelosIdaList = result.documents.mapNotNull { it.toObject(Vuelo::class.java) }
                    Log.d("ResultadosViewModel14", "✅ Vuelos de ida encontrados: ${vuelosIdaList.size}")
                    _vuelosIda.value = vuelosIdaList

                    if (fechaVuelta != null) {
                        db.collection("Vuelos")
                            .whereEqualTo("origen", destino)
                            .whereEqualTo("destino", origen)
                            .whereGreaterThanOrEqualTo("fechaSalida", fechaMinimaVuelta)
                            .whereLessThanOrEqualTo("fechaSalida", fechaVuelta)
                            .get()
                            .addOnSuccessListener { vueltaResult ->
                                val vuelosVueltaList = vueltaResult.documents.mapNotNull { it.toObject(Vuelo::class.java) }
                                Log.d("ResultadosViewModel15", "🔁 Vuelos de vuelta encontrados: ${vuelosVueltaList.size}")
                                _vuelosVuelta.value = vuelosVueltaList

                                generarCombinacionesValidas()
                            }
                            .addOnFailureListener { e ->
                                Log.e("ResultadosViewModel16", "❌ Error al cargar vuelos de vuelta", e)
                            }
                    } else {
                        _vuelosVuelta.value = emptyList()
                        _combinacionesValidas.value = emptyList()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ResultadosViewModel17", "❌ Error al cargar vuelos de ida", e)
                }
        }
    }



    /**
     * Genera combinaciones de ida y vuelta válidas.
     * Solo combina vuelos si la salida del vuelo de vuelta es posterior a la llegada del vuelo de ida.
     */
    private fun generarCombinacionesValidas() {
        val idaList = _vuelosIda.value
        val vueltaList = _vuelosVuelta.value

        val combinaciones = idaList.flatMap { ida ->
            vueltaList.filter { vuelta ->
                vuelta.fechaSalida.toDate().after(ida.fechaLlegada.toDate())
            }.map { vuelta -> ida to vuelta }
        }

        Log.d("ResultadosViewModel18", "🔗 Combinaciones válidas generadas: ${combinaciones.size}")
        _combinacionesValidas.value = combinaciones
    }
}
