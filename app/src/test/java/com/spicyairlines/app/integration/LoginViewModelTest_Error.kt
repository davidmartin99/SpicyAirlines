// LoginViewModelTest_Error.kt

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.spicyairlines.app.viewmodel.FakeLoginViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest_Error {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FakeLoginViewModel

    @Before
    fun setUp() {
        viewModel = FakeLoginViewModel()
    }

    @Test
    fun `login fails with incorrect credentials`() = runTest {
        viewModel.login("wrong@example.com", "wrongpassword")

        advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertEquals("Correo o contraseña incorrectos.", viewModel.error.value)

        // Mensaje de éxito si pasa el test
        println("Éxito: Test de login fallido completado correctamente. Las credenciales incorrectas fueron detectadas.")
    }

    @Test
    fun `login succeeds with correct credentials`() = runTest {
        viewModel.login("test@example.com", "password123")

        advanceUntilIdle()

        assertNull(viewModel.error.value)

        // Mensaje de éxito si pasa el test
        println("Éxito: Test de login exitoso completado correctamente. El usuario ha sido autenticado correctamente.")
    }
}
