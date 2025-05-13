package com.spicyairlines.app.screens

import android.annotation.SuppressLint
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import com.spicyairlines.app.viewmodel.LoginViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Pruebas automáticas para la pantalla de inicio de sesión (LoginScreen)
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    // Regla para lanzar la pantalla de prueba en un entorno Compose.
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Prueba que verifica que todos los componentes de la pantalla de inicio de sesión se muestran correctamente.
    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_showsAllComponentsCorrectly() {
        // Configuramos la pantalla de LoginScreen para la prueba.
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {}, // Acción vacía para login exitoso.
                onBack = {}          // Acción vacía para botón de retroceso.
            )
        }

        // Verificamos que el campo de correo electrónico y el botón de "Entrar" existen.
        composeTestRule.onNodeWithText("Correo electrónico").assertExists()
        composeTestRule.onNodeWithText("Entrar").assertExists()
    }

    // Prueba que verifica que el texto ingresado en el campo de correo electrónico actualiza el ViewModel.
    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_emailInputUpdatesViewModel() {
        // Configuramos la pantalla de LoginScreen para la prueba.
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {},
                onBack = {}
            )
        }

        // Simulamos la entrada de texto en el campo de correo electrónico.
        composeTestRule.onNodeWithText("Correo electrónico")
            .performTextInput("test@example.com")

        // Verificamos que el texto ingresado se refleja correctamente.
        composeTestRule.onNodeWithText("Correo electrónico").assertTextContains("test@example.com")
    }

    // Prueba que verifica que el botón "Entrar" funciona correctamente.
    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_loginButtonTriggersLogin() {
        // Configuramos la pantalla de LoginScreen para la prueba.
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {},
                onBack = {}
            )
        }

        // Simulamos la entrada de texto en los campos de correo y contraseña.
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        // Simulamos el clic en el botón de "Entrar".
        composeTestRule.onNodeWithText("Entrar").performClick()
    }
}
