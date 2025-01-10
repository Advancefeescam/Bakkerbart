package nl.bakkerbart.production

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class PaymentSuccessActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymentsucces)

        sharedPreferences = getSharedPreferences("OrderPreferences", MODE_PRIVATE)

        val konfettiView: KonfettiView = findViewById(R.id.konfetti_view)

        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )

        konfettiView.start(party)

        val backToHomeButton: Button = findViewById(R.id.backToHomeButton)
        backToHomeButton.setOnClickListener {
            resetOrderData()
            navigateToHome()
        }
    }

    private fun resetOrderData() {
        val editor = sharedPreferences.edit()
        val productIds = listOf(
            "belegdebroodjes_1", "belegdebroodjes_2", "belegdebroodjes_3",
            "panini_1", "panini_2", "panini_3"
        )
        productIds.forEach { productId ->
            editor.putInt(productId, 0)
        }
        editor.putFloat("globalTotalPrice", 0f)
        editor.putInt("globalTotalProducts", 0)
        editor.apply()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
