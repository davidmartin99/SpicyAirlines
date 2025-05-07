// src/androidTest/java/com/spicyairlines/app/screens/LoginScreenTest.kt
package com.spicyairlines.app.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spicyairlines.app.MainActivity
import com.spicyairlines.app.viewmodel.LoginViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Mock
    private lateinit var mockViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testLoginSuccess() {
        // Usando el mock creado
        composeTestRule.onNodeWithText("Correo electrónico").performClick()
        composeTestRule.onNodeWithText("Entrar").performClick()

        // Verificar que se llama al metodo de login
        verify(mockViewModel).login(anyString(), anyString())
    }
}
