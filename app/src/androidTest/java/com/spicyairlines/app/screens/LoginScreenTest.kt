// src/androidTest/java/com/spicyairlines/app/screens/LoginScreenTest.kt
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

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_showsAllComponentsCorrectly() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").assertExists()
        composeTestRule.onNodeWithText("Entrar").assertExists()
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_emailInputUpdatesViewModel() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico")
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithText("Correo electrónico").assertTextContains("test@example.com")
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun loginScreen_loginButtonTriggersLogin() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(),
                onLoginSuccess = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Entrar").performClick()
    }
}
