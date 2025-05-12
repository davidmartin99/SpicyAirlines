// FakeResultadosViewModelTest.kt
package com.spicyairlines.app.viewmodel

import com.spicyairlines.app.model.Vuelo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeResultadosViewModelTest {

    private lateinit var viewModel: FakeResultadosViewModel

    @Before
    fun setUp() {
        viewModel = FakeResultadosViewModel()
    }

    @Test
    fun `cargar vuelos ida y ordenar de menor a mayor`() = runTest {
        val vuelos = listOf(
            Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 200),
            Vuelo(id = "2", origen = "Madrid", destino = "Chengdu", precioBase = 100),
            Vuelo(id = "3", origen = "Madrid", destino = "Chengdu", precioBase = 150)
        )

        viewModel.cargarVuelosFake(vuelos)
        viewModel.ordenarVuelos("Menor a mayor")

        val ordenados = viewModel.vuelosIda.value

        assertEquals(100, ordenados[0].precioBase)
        assertEquals(150, ordenados[1].precioBase)
        assertEquals(200, ordenados[2].precioBase)

        println("Éxito: Test 'cargar vuelos ida y ordenar de menor a mayor' completado correctamente.")

    }

    @Test
    fun `cargar vuelos ida y vuelta y generar combinaciones`() = runTest {
        val vuelosIda = listOf(
            Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 200)
        )

        val vuelosVuelta = listOf(
            Vuelo(id = "2", origen = "Chengdu", destino = "Madrid", precioBase = 150)
        )

        viewModel.cargarVuelosFake(vuelosIda, vuelosVuelta)

        val combinaciones = viewModel.combinacionesValidas.value

        assertEquals(1, combinaciones.size)
        assertEquals("Madrid", combinaciones[0].first.origen)
        assertEquals("Chengdu", combinaciones[0].first.destino)
        assertEquals("Chengdu", combinaciones[0].second.origen)
        assertEquals("Madrid", combinaciones[0].second.destino)

        println("Éxito: Test 'cargar vuelos ida y vuelta y generar combinaciones' completado correctamente.")

    }

    @Test
    fun `ordenar combinaciones de mayor a menor`() = runTest {
        val vuelosIda = listOf(
            Vuelo(id = "1", origen = "Madrid", destino = "Chengdu", precioBase = 100),
            Vuelo(id = "2", origen = "Madrid", destino = "Chengdu", precioBase = 200)
        )

        val vuelosVuelta = listOf(
            Vuelo(id = "3", origen = "Chengdu", destino = "Madrid", precioBase = 150),
            Vuelo(id = "4", origen = "Chengdu", destino = "Madrid", precioBase = 100)
        )

        viewModel.cargarVuelosFake(vuelosIda, vuelosVuelta)
        viewModel.ordenarVuelos("Mayor a menor")

        val combinaciones = viewModel.combinacionesValidas.value
        val precioTotalPrimera = combinaciones[0].first.precioBase + combinaciones[0].second.precioBase
        val precioTotalSegunda = combinaciones[1].first.precioBase + combinaciones[1].second.precioBase

        assertTrue(precioTotalPrimera >= precioTotalSegunda)

        println("Éxito: Test 'ordenar combinaciones de mayor a menor' completado correctamente.")
    }
}
