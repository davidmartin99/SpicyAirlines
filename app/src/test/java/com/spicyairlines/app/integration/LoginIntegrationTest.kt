package com.spicyairlines.app.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.model.Usuario
import com.spicyairlines.app.viewmodel.LoginViewModel
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginIntegrationTest {

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
    fun `login integration should complete successfully`() = runTest {
        val mockUser = Usuario("mockUid", "Test User", "test@test.com")

        // Mock Firebase Auth
        val mockAuthTask = mockk<com.google.android.gms.tasks.Task<AuthResult>>(relaxed = true)
        val mockAuthResult = mockk<AuthResult>(relaxed = true)
        val mockFirebaseUser = mockk<FirebaseUser>(relaxed = true)

        // Configurar autenticación exitosa
        every { auth.signInWithEmailAndPassword("hola22@gmail.com", "123456") } returns mockAuthTask
        every { mockAuthTask.addOnSuccessListener(any()) } answers {
            val successListener = it.invocation.args[0] as com.google.android.gms.tasks.OnSuccessListener<AuthResult>
            successListener.onSuccess(mockAuthResult)
            mockAuthTask
        }

        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "mockUid"

        // Mock Firestore (Carga del usuario)
        val mockDocumentTask = mockk<com.google.android.gms.tasks.Task<DocumentSnapshot>>(relaxed = true)
        val mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

        every {
            firestore.collection("usuarios").document("mockUid").get()
        } returns mockDocumentTask

        every { mockDocumentTask.addOnSuccessListener(any()) } answers {
            val successListener = it.invocation.args[0] as com.google.android.gms.tasks.OnSuccessListener<DocumentSnapshot>
            successListener.onSuccess(mockDocumentSnapshot)
            mockDocumentTask
        }

        every { mockDocumentSnapshot.toObject(Usuario::class.java) } returns mockUser

        // Ejecutar el login
        viewModel.login("hola22@gmail.com", "123456") {}

        // Verificar que el usuario se cargó correctamente
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertNotNull(viewModel.usuario.value)
        assertEquals("Test User", viewModel.usuario.value?.nombre)

        println("Éxito: Test de integración de login completado correctamente.")
    }
}
