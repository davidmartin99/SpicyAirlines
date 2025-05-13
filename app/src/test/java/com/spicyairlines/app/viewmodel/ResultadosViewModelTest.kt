package com.spicyairlines.app.viewmodel

import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Clase de prueba para verificar el comportamiento del ViewModel de Resultados (FakeResultadosViewModel).
class FakeResultadosViewModelTest {

    // ViewModel que vamos a probar.
    private lateinit var viewModel: FakeResultadosViewModel

    // Configuración inicial antes de cada prueba.
    @Before
    fun setUp() {
        viewModel = FakeResultadosViewModel() // Inicializamos el ViewModel.
    }

    // Test que verifica que los vuelos de ida se cargan y se ordenan correctamente de menor a mayor precio.
    @Test
    fun `cargar vuelos ida y ordenar de menor a mayor`() = runTest {
        // Lista de vuelos de prueba.
        val vuelos = listOf(
            Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 200),
            Vuelo(id = "2", origen = "Madrid", destino = "Chengdu", precioBase = 100),
            Vuelo(id = "3", origen = "Madrid", destino = "Chengdu", precioBase = 150)
        )

        // Cargamos los vuelos en el ViewModel y los ordenamos.
        viewModel.cargarVuelosFake(vuelos)
        viewModel.ordenarVuelos("Menor a mayor")

        // Verificamos que están ordenados correctamente.
        val ordenados = viewModel.vuelosIda.value
        assertEquals(100, ordenados[0].precioBase)
        assertEquals(150, ordenados[1].precioBase)
        assertEquals(200, ordenados[2].precioBase)

        // Mensaje de éxito si pasa el test.
        println("Éxito: Test 'cargar vuelos ida y ordenar de menor a mayor' completado correctamente.")
    }

    // Test que verifica que se generan correctamente las combinaciones de vuelos de ida y vuelta.
    @Test
    fun `cargar vuelos ida y vuelta y generar combinaciones`() = runTest {
        // Listas de vuelos de ida y vuelta.
        val vuelosIda = listOf(Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 200))
        val vuelosVuelta = listOf(Vuelo(id = "2", origen = "Chengdu", destino = "Madrid", precioBase = 150))

        // Cargamos los vuelos y generamos combinaciones.
        viewModel.cargarVuelosFake(vuelosIda, vuelosVuelta)
        val combinaciones = viewModel.combinacionesValidas.value

        // Verificamos que se ha generado correctamente la combinación.
        assertEquals(1, combinaciones.size)
        assertEquals("Madrid", combinaciones[0].first.origen)
        assertEquals("Chengdu", combinaciones[0].first.destino)
        assertEquals("Chengdu", combinaciones[0].second.origen)
        assertEquals("Madrid", combinaciones[0].second.destino)

        // Mensaje de éxito si pasa el test.
        println("Éxito: Test 'cargar vuelos ida y vuelta y generar combinaciones' completado correctamente.")
    }

    // Test que verifica que las combinaciones se ordenan correctamente de mayor a menor precio.
    @Test
    fun `ordenar combinaciones de mayor a menor`() = runTest {
        // Listas de vuelos de ida y vuelta con precios distintos.
        val vuelosIda = listOf(
            Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 100),
            Vuelo(id = "2", origen = "Madrid", destino = "Chengdu", precioBase = 200)
        )

        val vuelosVuelta = listOf(
            Vuelo(id = "3", origen = "Chengdu", destino = "Madrid", precioBase = 150),
            Vuelo(id = "4", origen = "Chengdu", destino = "Madrid", precioBase = 100)
        )

        // Cargamos los vuelos y los ordenamos de mayor a menor.
        viewModel.cargarVuelosFake(vuelosIda, vuelosVuelta)
        viewModel.ordenarVuelos("Mayor a menor")

        // Verificamos que las combinaciones están ordenadas correctamente.
        val combinaciones = viewModel.combinacionesValidas.value
        val precioTotalPrimera = combinaciones[0].first.precioBase + combinaciones[0].second.precioBase
        val precioTotalSegunda = combinaciones[1].first.precioBase + combinaciones[1].second.precioBase

        assertTrue(precioTotalPrimera >= precioTotalSegunda)

        // Mensaje de éxito si pasa el test.
        println("Éxito: Test 'ordenar combinaciones de mayor a menor' completado correctamente.")
    }
}
