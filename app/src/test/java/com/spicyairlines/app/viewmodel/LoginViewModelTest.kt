// src/test/java/com/spicyairlines/app/viewmodel/LoginViewModelTest.kt
package com.spicyairlines.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doAnswer

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    @Mock
    private lateinit var documentSnapshot: DocumentSnapshot

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = LoginViewModel(auth, firestore)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `login success should load user`() = runTest {
        // Simular éxito de autenticación
        val mockAuthTask = mock(Task::class.java) as Task<AuthResult>
        val mockAuthResult = mock(AuthResult::class.java)

        // Configurar la autenticación para que devuelva el usuario simulado
        `when`(auth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthTask)
        `when`(mockAuthTask.addOnSuccessListener(any())).thenAnswer {
            (it.arguments[0] as (AuthResult) -> Unit).invoke(mockAuthResult)
            mockAuthTask
        }

        `when`(mockAuthResult.user).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUid")

        // Simular éxito de carga de usuario desde Firestore
        val mockDocumentTask = mock(Task::class.java) as Task<DocumentSnapshot>
        `when`(firestore.collection("usuarios").document("mockUid").get()).thenReturn(mockDocumentTask)

        `when`(mockDocumentTask.addOnSuccessListener(any())).thenAnswer {
            (it.arguments[0] as (DocumentSnapshot) -> Unit).invoke(documentSnapshot)
            mockDocumentTask
        }

        `when`(documentSnapshot.toObject(Usuario::class.java))
            .thenReturn(Usuario("mockUid", "Test User", "test@test.com"))

        // Ejecutar el login
        viewModel.login("hola22@gmail.com", "123456") {}

        // Verificar que se cargó correctamente el usuario
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertNotNull(viewModel.usuario.value)
        assertEquals("Test User", viewModel.usuario.value?.nombre)
    }
}
