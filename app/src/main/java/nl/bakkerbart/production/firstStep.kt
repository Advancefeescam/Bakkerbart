package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import nl.bakkerbart.production.R

class Step1Activity : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var nextButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_first)

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        nextButton = findViewById(R.id.nextButton)


        loadSavedData()

        nextButton.setOnClickListener {
            if (validateInputs()) {

                val editor = sharedPreferences.edit()
                editor.putString("firstName", firstNameInput.text.toString())
                editor.putString("lastName", lastNameInput.text.toString())
                editor.putString("phoneNumber", phoneNumberInput.text.toString())
                editor.apply()


                val intent = Intent(this, Step2Activity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val phoneNumber = phoneNumberInput.text.toString().trim()

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Voornaam is verplicht", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Achternaam is verplicht", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Telefoonnummer is verplicht", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Ongeldig telefoonnummer", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return Patterns.PHONE.matcher(phoneNumber).matches()
    }


    private fun loadSavedData() {
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val phoneNumber = sharedPreferences.getString("phoneNumber", "")

        firstNameInput.setText(firstName)
        lastNameInput.setText(lastName)
        phoneNumberInput.setText(phoneNumber)
    }
}
