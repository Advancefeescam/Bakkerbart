package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AccountInfo : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account_info, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)


        val email = sharedPreferences.getString("email", null)

        if (email != null) {

            val emailTextView: TextView = view.findViewById(R.id.accountEmail)
            emailTextView.text = "Email: $email"
        } else {
            Toast.makeText(requireContext(), "Geen e-mail gevonden", Toast.LENGTH_SHORT).show()
        }


        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        val editEmailButton: Button = view.findViewById(R.id.editEmailButton)
        editEmailButton.setOnClickListener {
            val intent = Intent(activity, ChangeEmailActivity::class.java)
            startActivity(intent)
        }


        val editPasswordButton: Button = view.findViewById(R.id.editPasswordButton)
        editPasswordButton.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }


        val eyeIconButton: ImageButton = view.findViewById(R.id.eyeIconButton)
        eyeIconButton.setOnClickListener {
            Toast.makeText(requireContext(), "Wachtwoord is verborgen voor veiligheid.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
