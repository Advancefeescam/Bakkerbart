package nl.bakkerbart.production

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkoutButton: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var noProductsInCartLayout: View
    private val cartItems = mutableListOf<CartItem>()

    private val productPrices = mapOf(
        "panini_1" to 5.0,
        "panini_2" to 10.0,
        "panini_3" to 15.0,
        "belegdebroodjes_1" to 5.0,
        "belegdebroodjes_2" to 10.0,
        "belegdebroodjes_3" to 15.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoppingcart)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.recyclerView)
        totalPriceTextView = findViewById(R.id.totalPrice)
        checkoutButton = findViewById(R.id.checkout)
        noProductsInCartLayout = findViewById(R.id.noProductsInCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadCartItems()

        cartAdapter = CartAdapter(this, cartItems)
        recyclerView.adapter = cartAdapter

        checkoutButton.setOnClickListener {
            val intent = Intent(this, Step1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun loadCartItems() {
        val productIds = listOf(
            "panini_1", "panini_2", "panini_3",
            "belegdebroodjes_1", "belegdebroodjes_2", "belegdebroodjes_3"
        )
        var totalPrice = 0.0

        cartItems.clear()

        productIds.forEach { productId ->
            val quantity = sharedPreferences.getInt(productId, 0)
            if (quantity > 0) {
                val title = sharedPreferences.getString("${productId}_title", "") ?: ""
                val description = sharedPreferences.getString("${productId}_description", "") ?: ""
                val imageResId = sharedPreferences.getInt("${productId}_image", 0)

                val price = productPrices[productId] ?: 0.0

                val cartItem = CartItem(title, description, price, quantity, imageResId)
                cartItems.add(cartItem)

                totalPrice += price * quantity
            }
        }

        totalPriceTextView.text = "Totaal: € ${"%.2f".format(totalPrice)}"

        if (cartItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            noProductsInCartLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noProductsInCartLayout.visibility = View.GONE
        }
    }
}
