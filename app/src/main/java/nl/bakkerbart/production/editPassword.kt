package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
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

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        val oldPasswordInput: EditText = findViewById(R.id.oldPasswordInput)
        val newPasswordInput: EditText = findViewById(R.id.newPasswordInput)
        val confirmPasswordInput: EditText = findViewById(R.id.confirmPasswordInput)
        val changePasswordButton: Button = findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val oldPassword = oldPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vul alstublieft alle velden in.", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "Wachtwoorden komen niet overeen.", Toast.LENGTH_SHORT).show()
            } else {
                if (!userEmail.isNullOrEmpty()) {
                    changePassword(userEmail, oldPassword, newPassword)
                } else {
                    Toast.makeText(this, "Gebruiker niet gevonden.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun changePassword(email: String, oldPassword: String, newPassword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://192.168.206.122/register_app/update_password.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "email=$email&old_password=$oldPassword&new_password=$newPassword"
                connection.outputStream.write(postData.toByteArray())

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getBoolean("success")
                        val message = jsonResponse.getString("message")

                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                        if (success) {
                            val intent = Intent(this@ChangePasswordActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Fout: $response", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Er is een fout opgetreden: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
