package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Step3Activity : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var paymentTypeSpinner: Spinner
    private lateinit var paymentMethodInput: EditText
    private lateinit var paymentAmountTextView: TextView
    private lateinit var confirmPaymentButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_final)

        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        paymentTypeSpinner = findViewById(R.id.paymentTypeSpinner)
        paymentMethodInput = findViewById(R.id.paymentMethodInput)
        paymentAmountTextView = findViewById(R.id.paymentAmountTextView)
        confirmPaymentButton = findViewById(R.id.payButton)

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        firstNameInput.setText(sharedPreferences.getString("firstName", ""))
        lastNameInput.setText(sharedPreferences.getString("lastName", ""))
        phoneNumberInput.setText(sharedPreferences.getString("phoneNumber", ""))

        val savedPaymentMethod = sharedPreferences.getString("selectedPaymentMethod", "")
        setPaymentMethodSelection(savedPaymentMethod ?: "Credit Card")

        val savedPaymentDetails = sharedPreferences.getString("paymentDetails", "")
        paymentMethodInput.setText(savedPaymentDetails)

        val globalTotalPrice = sharedPreferences.getFloat("globalTotalPrice", 0.0f)
        paymentAmountTextView.text = "Totaal Bedrag: € ${"%.2f".format(globalTotalPrice)}"

        setupPaymentTypeSpinner()

        confirmPaymentButton.setOnClickListener {
            if (validateInputs()) {
                savePaymentDetails()
                val intent = Intent(this, PaymentSuccessActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupPaymentTypeSpinner() {
        val paymentTypes = arrayOf("Credit Card", "PayPal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        paymentTypeSpinner.adapter = adapter

        // Set font for the spinner
        paymentTypeSpinner.setPopupBackgroundResource(android.R.color.white) // Ensure background is clean
        paymentTypeSpinner.adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            paymentTypes
        ) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.typeface = resources.getFont(R.font.bree_serif_regular)
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as? TextView)?.typeface = resources.getFont(R.font.bree_serif_regular)
                return view
            }
        }

        paymentTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateUIBasedOnPaymentMethod()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setPaymentMethodSelection(paymentMethod: String) {
        val paymentTypes = arrayOf("Credit Card", "PayPal")
        val selectedIndex = paymentTypes.indexOf(paymentMethod)
        if (selectedIndex != -1) {
            paymentTypeSpinner.setSelection(selectedIndex)
        }
    }

    private fun updateUIBasedOnPaymentMethod() {
        val selectedPaymentType = paymentTypeSpinner.selectedItem?.toString() ?: "Credit Card"
        when (selectedPaymentType) {
            "Credit Card" -> {
                paymentMethodInput.hint = "Enter Card Number"
                paymentMethodInput.visibility = EditText.VISIBLE
            }
            "PayPal" -> {
                paymentMethodInput.hint = "Enter PayPal Email"
                paymentMethodInput.visibility = EditText.VISIBLE
            }
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val phoneNumber = phoneNumberInput.text.toString().trim()
        val paymentMethod = paymentTypeSpinner.selectedItem.toString()
        val paymentDetails = paymentMethodInput.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            showToast("Vul alle velden voor persoonlijke gegevens in.")
            return false
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showToast("Ongeldig telefoonnummer.")
            return false
        }

        if (paymentMethod == "PayPal" && !isValidEmail(paymentDetails)) {
            showToast("Ongeldige PayPal e-mail.")
            return false
        }

        if (paymentMethod == "Credit Card" && !isValidCreditCard(paymentDetails)) {
            showToast("Ongeldig creditcardnummer.")
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidCreditCard(cardNumber: String): Boolean {
        val regex = "^\\d{16}$"
        return cardNumber.matches(regex.toRegex())
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = "^\\+?[0-9]{10,13}$"
        return phoneNumber.matches(regex.toRegex())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun savePaymentDetails() {
        val editor = sharedPreferences.edit()
        editor.putString("firstName", firstNameInput.text.toString())
        editor.putString("lastName", lastNameInput.text.toString())
        editor.putString("phoneNumber", phoneNumberInput.text.toString())

        val selectedPaymentMethod = paymentTypeSpinner.selectedItem.toString()
        val paymentDetails = paymentMethodInput.text.toString()

        editor.putString("selectedPaymentMethod", selectedPaymentMethod)
        editor.putString("paymentDetails", paymentDetails)
        editor.apply()
    }
}
