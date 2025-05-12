package com.spicyairlines.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        auth = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        viewModel = LoginViewModel(auth, firestore)
    }

    @Test
    fun `login success should load user`() = runTest {
        // Simular éxito de autenticación
        val mockAuthResult = mockk<AuthResult>(relaxed = true)
        val firebaseUser = mockk<FirebaseUser>(relaxed = true)
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        val usuario = Usuario("mockUid", "Test User", "test@test.com")

        // Configurar Firebase Auth para devolver el usuario
        val mockAuthTask = mockk<com.google.android.gms.tasks.Task<AuthResult>>(relaxed = true)
        every { auth.signInWithEmailAndPassword(any(), any()) } returns mockAuthTask

        // Configurar el listener de éxito de autenticación
        every { mockAuthTask.addOnSuccessListener(any<OnSuccessListener<AuthResult>>()) } answers {
            firstArg<OnSuccessListener<AuthResult>>().onSuccess(mockAuthResult)
            mockAuthTask
        }

        every { mockAuthResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "mockUid"

        // Configurar Firestore para cargar el usuario
        val mockDocumentTask = mockk<com.google.android.gms.tasks.Task<DocumentSnapshot>>(relaxed = true)
        every {
            firestore.collection("usuarios").document("mockUid").get()
        } returns mockDocumentTask

        // Configurar el listener de éxito en Firestore
        every { mockDocumentTask.addOnSuccessListener(any<OnSuccessListener<DocumentSnapshot>>()) } answers {
            firstArg<OnSuccessListener<DocumentSnapshot>>().onSuccess(documentSnapshot)
            mockDocumentTask
        }

        every { documentSnapshot.toObject(Usuario::class.java) } returns usuario

        // EjecutarA el login
        viewModel.login("hola22@gmail.com", "123456") {}

        // Verificar que se cargó correctamente el usuario
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertNotNull(viewModel.usuario.value)
        assertEquals("Test User", viewModel.usuario.value?.nombre)

        println("Éxito: Test de login correcto.")
    }

    @Test
    fun `login failure should set error message`() = runTest {
        // Simular error de autenticación
        val mockAuthTask = mockk<com.google.android.gms.tasks.Task<AuthResult>>(relaxed = true)
        every { auth.signInWithEmailAndPassword(any(), any()) } returns mockAuthTask

        // Configurar el Task para fallar directamente
        every { mockAuthTask.addOnSuccessListener(any()) } returns mockAuthTask // No llama a success
        every { mockAuthTask.addOnFailureListener(any<OnFailureListener>()) } answers {
            firstArg<OnFailureListener>().onFailure(Exception("Authentication failed"))
            mockAuthTask
        }

        // Ejecutar el login
        viewModel.login("invalid@gmail.com", "wrongpassword") {}

        // Verificar que se cargó correctamente el mensaje de error
        assertNotNull(viewModel.error.value)
        assertEquals("Correo o contraseña incorrectos.", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)

        println("Éxito: Test de fallo de login correcto.")
    }

}
