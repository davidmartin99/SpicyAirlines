// src/androidTest/java/com/spicyairlines/app/screens/ResultadosScreenPerformanceTest.kt
package com.spicyairlines.app.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import com.spicyairlines.app.viewmodel.ResultadosViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.util.Log
import kotlin.system.measureTimeMillis

// Clase de prueba para verificar el rendimiento de la carga de resultados de vuelos.
@RunWith(AndroidJUnit4::class)
class ResultadosScreenPerformanceTest {

    // Regla para lanzar la pantalla de prueba en un entorno Compose.
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Etiqueta para los mensajes de log (consola)
    private val TAG = "TestPerformance"

    // Test que mide el tiempo de carga de resultados de vuelos, asegurando que sea menor a 15 segundos.
    @Test
    fun cargaResultadosReal_enMenosDe15Segundos() = runBlocking {
        try {
            // Creamos una instancia del ViewModel de Resultados.
            val viewModel = ResultadosViewModel()

            var tiempoCarga = 0L // Variable para almacenar el tiempo de carga.

            // Medimos el tiempo que tarda en cargar los vuelos.
            tiempoCarga = measureTimeMillis {
                // Llamamos al metodo de carga de vuelos con parámetros de prueba.
                viewModel.cargarVuelos(
                    origen = "Madrid",
                    destino = "Chengdu",
                    fechaIda = Timestamp.now(),
                    fechaVuelta = Timestamp.now().toDate().time.plus(1000 * 60 * 60 * 24 * 2)
                        .let { Timestamp(it / 1000, 0) }, // Fecha de vuelta 2 días después.
                    claseSeleccionada = "Turista", // Clase de vuelo seleccionada.
                    totalPasajeros = 1 // Total de pasajeros (1).
                )

                // Esperamos a que la carga se complete, con un tiempo máximo de 15 segundos.
                withTimeoutOrNull(15000) {
                    viewModel.cargaCompletada.first { it } // Esperamos hasta que se complete la carga.
                }
            }

            // Mostramos el tiempo de carga en la consola (Log y System.out).
            Log.d(TAG, "Tiempo de carga de resultados (real): ${tiempoCarga} ms")
            System.out.println("Tiempo de carga de resultados (real): ${tiempoCarga} ms")

            // Verificamos si el tiempo de carga es menor o igual a 15 segundos.
            if (tiempoCarga <= 15000) {
                Log.d(TAG, "Éxito: Los resultados se cargaron en ${tiempoCarga} ms")
                System.out.println("Éxito: Los resultados se cargaron en ${tiempoCarga} ms")
            } else {
                Log.d(TAG, "Error: La carga de resultados tardó demasiado (${tiempoCarga} ms)")
                System.out.println("Error: La carga de resultados tardó demasiado (${tiempoCarga} ms)")
                assert(false) { "Error: La carga de resultados tardó demasiado (${tiempoCarga} ms)" }
            }
        } catch (e: Exception) {
            // En caso de que ocurra alguna excepción durante el test.
            Log.e(TAG, "Excepción durante el test: ${e.message}")
            System.out.println("Excepción durante el test: ${e.message}")
            assert(false) { "Excepción durante el test: ${e.message}" }
        }
    }
}
