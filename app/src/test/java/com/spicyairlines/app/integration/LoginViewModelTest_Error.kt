// src/test/java/com/spicyairlines/app/viewmodel/LoginViewModelTest_Error.kt
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.spicyairlines.app.viewmodel.FakeLoginViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Clase de prueba para verificar el funcionamiento del ViewModel de login.
class LoginViewModelTest_Error {

    // Regla para ejecutar tareas en el hilo principal de forma instantánea durante las pruebas.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Instancia del ViewModel que se va a probar.
    private lateinit var viewModel: FakeLoginViewModel

    // Configuración inicial antes de cada test.
    @Before
    fun setUp() {
        viewModel = FakeLoginViewModel() // Inicializamos el ViewModel.
    }

    // Test que verifica que el login falla cuando las credenciales son incorrectas.
    @Test
    fun `login fails with incorrect credentials`() = runTest {
        // Intentamos iniciar sesión con credenciales incorrectas.
        viewModel.login("wrong@example.com", "wrongpassword")

        // Esperamos a que se complete cualquier tarea pendiente.
        advanceUntilIdle()

        // Verificamos que haya un mensaje de error.
        assertNotNull(viewModel.error.value)
        assertEquals("Correo o contraseña incorrectos.", viewModel.error.value)

        // Mensaje de éxito en consola si el test pasa correctamente.
        println("✅ Éxito: Test de login fallido completado correctamente. Las credenciales incorrectas fueron detectadas.")
    }

    // Test que verifica que el login es exitoso con credenciales correctas.
    @Test
    fun `login succeeds with correct credentials`() = runTest {
        // Intentamos iniciar sesión con credenciales correctas.
        viewModel.login("test@example.com", "password123")

        // Esperamos a que se complete cualquier tarea pendiente.
        advanceUntilIdle()

        // Verificamos que no haya error.
        assertNull(viewModel.error.value)

        // Mensaje de éxito en consola si el test pasa correctamente.
        println("✅ Éxito: Test de login exitoso completado correctamente. El usuario ha sido autenticado correctamente.")
    }
}
