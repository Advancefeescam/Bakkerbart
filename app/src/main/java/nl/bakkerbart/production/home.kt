package nl.bakkerbart.production

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import nl.bakkerbart.production.fragments.belegde_broodjes
import nl.bakkerbart.production.fragments.panini
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    private val productCategoryMap = mapOf(
        "Belegde broodjes" to R.id.categoryButtonBELEG,
        "Panini's en Tosti's" to R.id.categoryButtonTOSTI,
        "Ontbijt en Lunch" to R.id.categoryButtonLUNCH,
        "Hartige snacks" to R.id.categoryButtonSNACKS,
        "Zoete snacks" to R.id.categoryButtonZOETSNACK
    )

    private lateinit var searchBar: EditText
    private lateinit var productSuggestionsLayout: LinearLayout
    private lateinit var productSuggestionsAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeGreeting = findViewById<TextView>(R.id.homeGreeting)
        val email = sharedPreferences.getString("email", "")
        val firstName = getFirstNameFromEmail(email)

        val greetingText = getGreetingText()
        homeGreeting.text = if (email.isNullOrEmpty()) {
            "$greetingText"
        } else {
            "$greetingText, $firstName"
        }

        findViewById<ImageButton>(R.id.profileButton).setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.shoppingCartButton).setOnClickListener {
            val intent = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val categoryButtonBeleg = findViewById<Button>(R.id.categoryButtonBELEG)
        val categoryButtonTosti = findViewById<Button>(R.id.categoryButtonTOSTI)

        categoryButtonBeleg.setOnClickListener {
            selectCategory(categoryButtonBeleg)
            switchFragment(belegde_broodjes())
            saveLastFragment("belegde_broodjes")
        }

        categoryButtonTosti.setOnClickListener {
            selectCategory(categoryButtonTosti)
            switchFragment(panini())
            saveLastFragment("panini")
        }

        searchBar = findViewById(R.id.searchBar)
        productSuggestionsLayout = findViewById(R.id.searchSuggestionsLayout)

        productSuggestionsAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutableListOf()) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)


                textView.typeface = ResourcesCompat.getFont(context, R.font.bree_serif_regular)


                textView.setTextColor(resources.getColor(android.R.color.darker_gray))

                return view
            }
        }

        val productSuggestionsListView = ListView(this)
        productSuggestionsListView.adapter = productSuggestionsAdapter

        productSuggestionsLayout.addView(productSuggestionsListView)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString().trim()
                if (query.isNotEmpty()) {
                    showProductSuggestions(query)
                } else {
                    productSuggestionsLayout.visibility = View.GONE
                }
            }
        })

        productSuggestionsListView.setOnItemClickListener { parent, view, position, id ->
            val product = parent.getItemAtPosition(position) as String
            navigateToCategory(product)
        }

        if (savedInstanceState == null) {
            checkAndShowCancelOrderDialog()
        }

        displayTotalPriceAndCount()
    }

    private fun getGreetingText(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11 -> "Goedemorgen"
            hour in 12..17 -> "Goedemiddag"
            else -> "Goedenavond"
        }
    }

    private fun getFirstNameFromEmail(email: String?): String {
        return email?.substringBefore("@") ?: "Gast"
    }

    private fun saveLastFragment(fragmentName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("lastFragment", fragmentName)
        editor.apply()
    }

    private fun showProductSuggestions(query: String) {

        val matchingProducts = getMatchingProducts(query)

        if (matchingProducts.isNotEmpty()) {
            productSuggestionsAdapter.clear()
            productSuggestionsAdapter.addAll(matchingProducts)
            productSuggestionsLayout.visibility = View.VISIBLE
        } else {
            productSuggestionsLayout.visibility = View.GONE
        }
    }

    private fun getMatchingProducts(query: String): List<String> {

        val allProducts = listOf("Belegde broodjes", "Panini's en Tosti's", "Ontbijt en Lunch", "Hartige snacks", "Zoete snacks")
        return allProducts.filter { it.contains(query, ignoreCase = true) }
    }

    private fun navigateToCategory(product: String) {

        val categoryId = productCategoryMap[product]
        categoryId?.let {
            val categoryButton = findViewById<Button>(it)
            selectCategory(categoryButton)

            when (categoryButton.id) {
                R.id.categoryButtonBELEG -> switchFragment(belegde_broodjes())
                R.id.categoryButtonTOSTI -> switchFragment(panini())
            }

            productSuggestionsLayout.visibility = View.GONE
        }
    }

    private fun hideNoCategoryMessage() {
        val noCategoryText = findViewById<TextView>(R.id.noCategoryText)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)

        noCategoryText.visibility = View.GONE
        imageView2.visibility = View.GONE
    }

    private fun switchFragment(fragment: Fragment) {
        hideNoCategoryMessage()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewPager, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun selectCategory(button: Button) {
        val categoryButtons = listOf(
            findViewById<Button>(R.id.categoryButtonBELEG),
            findViewById<Button>(R.id.categoryButtonTOSTI),
            findViewById<Button>(R.id.categoryButtonLUNCH),
            findViewById<Button>(R.id.categoryButtonSNACKS),
            findViewById<Button>(R.id.categoryButtonZOETSNACK)
        )

        categoryButtons.forEach {
            it.setBackgroundColor(resources.getColor(android.R.color.transparent))
            it.setTextColor(resources.getColor(R.color.bakkerBartPink))
            it.textSize = 14f
        }

        button.setBackgroundColor(resources.getColor(R.color.bakkerBartPink))
        button.setTextColor(resources.getColor(android.R.color.white))
        button.textSize = 12f
    }

    fun updateUI() {
        val totalPrice = sharedPreferences.getFloat("globalTotalPrice", 0f).toDouble()
        val totalProducts = sharedPreferences.getInt("globalTotalProducts", 0)

        val totalPriceTextView = findViewById<TextView>(R.id.totalPrice)
        val totalProductsTextView = findViewById<TextView>(R.id.productCount1)

        totalPriceTextView.text = "€${"%.2f".format(totalPrice)}"
        if (totalProducts == 1) {
            totalProductsTextView.text = "$totalProducts Product"
        } else {
            totalProductsTextView.text = "$totalProducts Producten"
        }
    }

    private fun checkAndShowCancelOrderDialog() {
        val totalPrice = sharedPreferences.getFloat("globalTotalPrice", 0f)
        val totalProducts = sharedPreferences.getInt("globalTotalProducts", 0)

        if (totalPrice > 0 || totalProducts > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Er zijn nog producten in je winkelwagentje")
            builder.setMessage("Wil je de bestelling annuleren of verder gaan?")

            builder.setPositiveButton("Verder gaan") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setNegativeButton("Annuleren") { dialog, _ ->
                dialog.dismiss()
                resetOrderData()
            }

            builder.setCancelable(false)

            builder.create().show()
        } else {
            checkForInvalidDataAndShowError()
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

        displayTotalPriceAndCount()

        Toast.makeText(this, "Bestelling geannuleerd.", Toast.LENGTH_SHORT).show()
    }

    private fun displayTotalPriceAndCount() {
        updateUI()
    }

    private fun checkForInvalidDataAndShowError() {
        val totalPrice = sharedPreferences.getFloat("globalTotalPrice", 0f)
        val totalProducts = sharedPreferences.getInt("globalTotalProducts", 0)

        if (totalPrice < 0 || totalProducts < 0) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Er is een fout opgetreden")
            builder.setMessage("Er is een probleem met de bestelling. De bestelling wordt geannuleerd voor veiligheid.")

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                resetOrderData()
            }

            builder.create().show()
        }
    }
}
