import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import com.google.firebase.auth.AuthResult
import com.spicyairlines.app.viewmodel.LoginViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spicyairlines.app.screens.LoginScreen
import io.mockk.every
import io.mockk.mockk

@RunWith(AndroidJUnit4::class)
class LoginScreenTest_Error {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: LoginViewModel

    @Test
    fun loginScreen_showsErrorForInvalidCredentials() {
        // Configurar FirebaseAuth y Firestore Mockeados
        auth = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        viewModel = LoginViewModel(auth, firestore)

        val email = "wrong@example.com"
        val password = "wrongpassword"

        // Simular fallo de autenticación
        val authTask = mockk<com.google.android.gms.tasks.Task<AuthResult>>(relaxed = true)
        every { auth.signInWithEmailAndPassword(email, password) } returns authTask

        // Simular el listener de fallo
        every { authTask.addOnFailureListener(any()) } answers {
            firstArg<(Exception) -> Unit>().invoke(Exception("Correo o contraseña incorrectos."))
            authTask
        }


        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel, onLoginSuccess = {}, onBack = {})
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput(email)
        composeTestRule.onNodeWithText("Contraseña").performTextInput(password)
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.onNodeWithText("Correo o contraseña incorrectos.").assertExists()
    }
}
