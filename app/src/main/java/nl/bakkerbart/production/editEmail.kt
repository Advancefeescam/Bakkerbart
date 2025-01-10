package nl.bakkerbart.production

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

class ChangeEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("email", "") ?: ""

        val newEmailInput: EditText = findViewById(R.id.newEmailInput)
        val confirmNewEmailInput: EditText = findViewById(R.id.confirmNewEmailInput)
        val changeEmailButton: Button = findViewById(R.id.changeEmailButton)


        changeEmailButton.setOnClickListener {
            val newEmail = newEmailInput.text.toString()
            val confirmNewEmail = confirmNewEmailInput.text.toString()

            if (newEmail.isEmpty() || confirmNewEmail.isEmpty()) {
                Toast.makeText(this, "Vul beide e-mailvelden in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newEmail != confirmNewEmail) {
                Toast.makeText(this, "E-mailadressen komen niet overeen.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (currentEmail.isNotEmpty()) {
                updateEmail(currentEmail, newEmail)
            } else {
                Toast.makeText(
                    this,
                    "Er is een fout opgetreden. Probeer opnieuw.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateEmail(currentEmail: String, newEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://192.168.206.122/register_app/update_email.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = "current_email=$currentEmail&new_email=$newEmail"
                connection.outputStream.write(postData.toByteArray())

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()


                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                            val message = jsonResponse.getString("message")


                            Toast.makeText(this@ChangeEmailActivity, message, Toast.LENGTH_SHORT)
                                .show()

                            if (success) {

                                val sharedPreferences =
                                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("email", newEmail)
                                editor.apply()

                                finish()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@ChangeEmailActivity,
                                "Fout bij het verwerken van het antwoord.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {

                        Toast.makeText(
                            this@ChangeEmailActivity,
                            "Serverfout: $responseCode",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ChangeEmailActivity,
                        "Fout bij verbinden met de server.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}