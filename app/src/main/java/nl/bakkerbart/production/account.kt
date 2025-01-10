package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.bakkerbart.production.adapters.TabsPagerAdapterACCOUNT
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)


        val signedIn = sharedPreferences.getBoolean("signed_in", false)
        if (!signedIn) {

            Toast.makeText(this, "Log in of maak een account om het beheer te gebruiken.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        this.setupTabs()

    }

    private fun setupTabs() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)


        val adapter = TabsPagerAdapterACCOUNT(this)
        viewPager.adapter = adapter


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Accountinformatie"
                1 -> "Privacy"
                else -> "Unknown"
            }
        }.attach()
    }

        }


