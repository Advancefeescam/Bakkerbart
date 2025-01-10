package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity

class Step2Activity : AppCompatActivity() {

    private lateinit var paymentTypeSpinner: Spinner
    private lateinit var paymentMethodInput: EditText
    private lateinit var payButton: Button
    private lateinit var paymentAmountTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_second)

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        paymentTypeSpinner = findViewById(R.id.paymentTypeSpinner)
        paymentMethodInput = findViewById(R.id.paymentMethodInput)
        payButton = findViewById(R.id.payButton)
        paymentAmountTextView = findViewById(R.id.paymentAmount)

        val paymentTypes = arrayOf("Credit Card", "PayPal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        paymentTypeSpinner.adapter = adapter
        paymentTypeSpinner.setPopupBackgroundResource(android.R.color.white)

        val globalTotalPrice = sharedPreferences.getFloat("globalTotalPrice", 0.0f)
        paymentAmountTextView.text = "Totaal bedrag: € ${"%.2f".format(globalTotalPrice)}"

        paymentTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPaymentType = parentView?.getItemAtPosition(position).toString()
                updateUIBasedOnPaymentMethod(selectedPaymentType)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        payButton.setOnClickListener {
            val selectedPaymentType = paymentTypeSpinner.selectedItem.toString()
            val paymentDetails = paymentMethodInput.text.toString()

            if (selectedPaymentType == "PayPal") {
                if (isValidEmail(paymentDetails)) {
                    savePaymentInfo("PayPal", paymentDetails)
                    navigateToNextStep()
                } else {
                    Toast.makeText(this, "Ongeldige email", Toast.LENGTH_SHORT).show()
                }
            } else if (selectedPaymentType == "Credit Card") {
                if (isValidCardNumber(paymentDetails)) {
                    savePaymentInfo("Credit Card", paymentDetails)
                    navigateToNextStep()
                } else {
                    Toast.makeText(this, "Ongeldig creditcardnummer", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vul alle velden in", Toast.LENGTH_SHORT).show()
            }
        }

        loadSavedData()
    }

    private fun updateUIBasedOnPaymentMethod(paymentMethod: String) {
        when (paymentMethod) {
            "Credit Card" -> {
                paymentMethodInput.hint = "Voer creditcardnummer in"
                paymentMethodInput.visibility = View.VISIBLE
            }
            "PayPal" -> {
                paymentMethodInput.hint = "Voer PayPal emailadress in"
                paymentMethodInput.visibility = View.VISIBLE
            }
        }
    }

    private fun savePaymentInfo(paymentMethod: String, paymentDetails: String) {
        val editor = sharedPreferences.edit()
        editor.putString("selectedPaymentMethod", paymentMethod)
        editor.putString("paymentDetails", paymentDetails)
        editor.apply()
    }

    private fun loadSavedData() {
        val savedPaymentMethod = sharedPreferences.getString("selectedPaymentMethod", "")
        val savedPaymentDetails = sharedPreferences.getString("paymentDetails", "")

        val paymentTypes = arrayOf("Credit Card", "PayPal")
        val selectedPaymentMethod = savedPaymentMethod ?: "Credit Card"
        val paymentTypeIndex = paymentTypes.indexOf(selectedPaymentMethod)
        paymentTypeSpinner.setSelection(paymentTypeIndex)

        updateUIBasedOnPaymentMethod(selectedPaymentMethod)

        paymentMethodInput.setText(savedPaymentDetails)
    }

    private fun navigateToNextStep() {
        val intent = Intent(this, Step3Activity::class.java)
        startActivity(intent)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains("@")
    }

    private fun isValidCardNumber(cardNumber: String): Boolean {
        val sanitizedCardNumber = cardNumber.replace(" ", "")
        if (sanitizedCardNumber.length < 13 || sanitizedCardNumber.length > 19) return false

        var sum = 0
        var shouldDouble = false
        for (i in sanitizedCardNumber.length - 1 downTo 0) {
            var digit = sanitizedCardNumber[i].toString().toInt()
            if (shouldDouble) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
            shouldDouble = !shouldDouble
        }
        return sum % 10 == 0
    }
}
