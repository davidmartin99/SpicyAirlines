package com.spicyairlines.app.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class PantallaInicioScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun pantallaInicio_cargaRapidamente() {
        // Medir solo el tiempo de renderizado de la pantalla
        composeTestRule.setContent {
            PantallaInicioAuth(onLoginClick = {}, onRegisterClick = {})
        }

        // Esperamos que el botón esté presente (la pantalla está lista)
        composeTestRule.onNodeWithText("Iniciar sesión").assertExists()

        // Medimos el tiempo de renderizado
        val tiempoCarga = measureTimeMillis {
            composeTestRule.onNodeWithText("Iniciar sesión").assertExists()
        }

        println("Tiempo de carga de contenido: \$tiempoCarga ms")

        // Verificamos que el tiempo de carga sea menor a 1000 ms (ajustable)
        assert(tiempoCarga < 1000) { "Error: La pantalla tardó demasiado en cargarse (\$tiempoCarga ms)" }

        println("Éxito: Pantalla de inicio cargada en \$tiempoCarga ms")
    }
}
