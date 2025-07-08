package garcia.carlosdamian.practica10_garciacarlosdamian

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class WelcomeActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        setupGoogleSignInClient()
        setupUI()
    }

    private fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupUI() {
        val evCorreo: TextView = findViewById(R.id.evCorreo)
        val evProveedor: TextView = findViewById(R.id.evProveedor)

        // Obtener datos del Intent o, si no existen, de SharedPreferences
        val mail = intent.getStringExtra("mail") ?: sharedPreferences.getString("user_email", "No disponible")
        val provider = intent.getStringExtra("provider") ?: sharedPreferences.getString("user_provider", "No disponible")

        evCorreo.text = "Correo: $mail"
        evProveedor.text = "Proveedor: $provider"

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val provider = sharedPreferences.getString("user_provider", null)

        // Limpiar la sesión guardada
        clearUserSession()

        if (provider == "Google") {
            // Cerrar sesión de Google
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    goToMainActivity()
                }
            }
        } else {
            // Para otros proveedores, solo ir a la pantalla principal
            goToMainActivity()
        }
    }

    private fun clearUserSession() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    private fun goToMainActivity() {
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        // Flags para limpiar el stack de actividades y evitar que el usuario regrese
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}