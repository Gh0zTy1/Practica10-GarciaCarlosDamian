package garcia.carlosdamian.practica10_garciacarlosdamian

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Si el usuario ya inició sesión, ir directamente a la pantalla de bienvenida
        if (isUserLoggedIn()) {
            goToWelcomeActivity()
            return
        }

        setContentView(R.layout.activity_main)
        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    private fun setupGoogleSignIn() {
        // Configura Google Sign-In para solicitar el email del usuario
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClickListeners() {
        findViewById<MaterialCardView>(R.id.btnLoginGoogle).setOnClickListener {
            signInWithGoogle()
        }

        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener {
            loginWithEmailPassword()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginWithEmailPassword() {
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Lógica de autenticación local (simulada)
            saveUserSession(email, "Email/Password")
            goToWelcomeActivity(email, "Email/Password")
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data)
        }
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val email = account.email ?: "No disponible"
            val provider = "Google"

            saveUserSession(email, provider)
            goToWelcomeActivity(email, provider)

        } catch (e: ApiException) {
            Toast.makeText(this, "Error al iniciar sesión con Google: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserSession(email: String, provider: String) {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true)
            putString("user_email", email)
            putString("user_provider", provider)
            apply()
        }
    }

    private fun goToWelcomeActivity(email: String? = null, provider: String? = null) {
        val intent = Intent(this, WelcomeActivity::class.java).apply {
            // Si no se pasan los datos, se obtendrán de SharedPreferences en WelcomeActivity
            email?.let { putExtra("mail", it) }
            provider?.let { putExtra("provider", it) }
        }
        startActivity(intent)
        finish() // Finaliza MainActivity para que el usuario no pueda volver con el botón "Atrás"
    }

    override fun onStart() {
        super.onStart()
        // Comprobar si el usuario ya ha iniciado sesión en este dispositivo con Google
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && !isUserLoggedIn()) {
            // Si hay una cuenta de Google pero no una sesión activa, iniciar sesión automáticamente
            val email = account.email ?: "No disponible"
            saveUserSession(email, "Google")
            goToWelcomeActivity(email, "Google")
        }
    }
}