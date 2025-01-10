package nl.bakkerbart.production

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PrivacyFragment : Fragment() {

    private lateinit var sellDataCheckbox: CheckBox
    private lateinit var marketingEmailsCheckbox: CheckBox
    private lateinit var dataSharingCheckbox: CheckBox
    private lateinit var thirdPartyCookiesCheckbox: CheckBox

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_privacy, container, false)

        sellDataCheckbox = view.findViewById(R.id.sellDataCheckbox)
        marketingEmailsCheckbox = view.findViewById(R.id.marketingEmailsCheckbox)
        dataSharingCheckbox = view.findViewById(R.id.marketingEmailsCheckbox3)
        thirdPartyCookiesCheckbox = view.findViewById(R.id.marketingEmailsCheckbox9)

        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("email", "") ?: ""

        getPrivacySettings()

        view.findViewById<View>(R.id.privacyButton).setOnClickListener {
            sendPrivacySettings()
        }

        return view
    }

    private fun sendPrivacySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://192.168.50.121/register_app/save_privacy_settings.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val sellData = if (sellDataCheckbox.isChecked) 1 else 0
                val marketingEmails = if (marketingEmailsCheckbox.isChecked) 1 else 0
                val dataSharing = if (dataSharingCheckbox.isChecked) 1 else 0
                val thirdPartyCookies = if (thirdPartyCookiesCheckbox.isChecked) 1 else 0

                val postData = "email=$userEmail" +
                        "&sell_data=$sellData" +
                        "&marketing_emails=$marketingEmails" +
                        "&data_sharing=$dataSharing" +
                        "&third_party_cookies=$thirdPartyCookies"

                connection.doOutput = true
                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                            val message = jsonResponse.getString("message")

                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Fout bij het parseren van het antwoord: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Fout: ${responseCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Er is een fout opgetreden: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPrivacySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://192.168.206.122/register_app/get_privacy_settings.php?email=$userEmail")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                            if (success) {
                                val sellData = jsonResponse.getInt("sell_data")
                                val marketingEmails = jsonResponse.getInt("marketing_emails")
                                val dataSharing = jsonResponse.getInt("data_sharing")
                                val thirdPartyCookies = jsonResponse.getInt("third_party_cookies")

                                sellDataCheckbox.isChecked = sellData == 1
                                marketingEmailsCheckbox.isChecked = marketingEmails == 1
                                dataSharingCheckbox.isChecked = dataSharing == 1
                                thirdPartyCookiesCheckbox.isChecked = thirdPartyCookies == 1
                            } else {
                                Toast.makeText(requireContext(), "Privacy-instellingen konden niet worden geladen.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Fout bij het parseren van het antwoord: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Fout: ${responseCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Er is een fout opgetreden: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}