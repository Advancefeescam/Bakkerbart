package nl.bakkerbart.production

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val confirmPasswordInput: EditText = findViewById(R.id.confirmPasswordInput)
        val signupButton: Button = findViewById(R.id.signupButton)
        val privacyButton: Button = findViewById(R.id.privacyButton)

        signupButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    saveToServer(email, password)
                } else {
                    Toast.makeText(this, "Wachtwoorden komen niet overeen", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Vul alstublieft alle velden in", Toast.LENGTH_SHORT).show()
            }
        }

        privacyButton.setOnClickListener {
            val intent = Intent(this@Register, Privacyvoorwaarden::class.java)
            startActivity(intent)
        }

    }

    private fun saveToServer(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://192.168.206.122/register_app/register.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.doOutput = true

                val jsonPayload = """{"email":"$email", "password":"$password"}"""
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(jsonPayload)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = JSONObject(response)
                        val status = jsonResponse.getString("status")

                        if (status == "success") {
                            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("signed_in", true)
                            editor.putString("email", email)
                            editor.apply()


                            val intent = Intent(this@Register, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val message = jsonResponse.getString("message")
                            Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Register,
                            "Registratie mislukt, probeer het opnieuw.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Register, "Fout: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
