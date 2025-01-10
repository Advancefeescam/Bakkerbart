package nl.bakkerbart.production


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Vul beide velden in", Toast.LENGTH_SHORT).show()
            }
        }

        val privacyButton: Button = findViewById(R.id.privacyButton)
        privacyButton.setOnClickListener {
            val intent = Intent(this@login, Privacyvoorwaarden::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val url = URL("http://192.168.206.122/register_app/login.php")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            val postData = "email=$email&password=$password"
            val outputStream = connection.outputStream
            outputStream.write(postData.toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            val response = connection.inputStream.bufferedReader().readText()

            withContext(Dispatchers.Main) {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val userId = jsonResponse.getInt("user_id")
                        val userEmail = jsonResponse.getString("email")

                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("user_id", userId)
                        editor.putString("email", userEmail)
                        editor.putBoolean("signed_in", true)
                        editor.apply()

                        val intent = Intent(this@login, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = jsonResponse.getString("message")
                        Toast.makeText(this@login, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@login,
                        "Fout bij verbinden met server",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
