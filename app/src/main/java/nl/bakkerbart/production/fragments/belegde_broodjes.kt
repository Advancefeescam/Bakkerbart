package nl.bakkerbart.production.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import nl.bakkerbart.production.HomeActivity
import nl.bakkerbart.production.R

class belegde_broodjes : Fragment() {

    private val productPrices = mapOf(
        "product1" to 5.0,
        "product2" to 10.0,
        "product3" to 15.0
    )

    private val productIds = listOf("product1", "product2", "product3")

    private val productImages = mapOf(
        "product1" to R.drawable.brie_broodje,
        "product2" to R.drawable.zalmpje,
        "product3" to R.drawable.baconei
    )

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.belegde_broodjes, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val customFont = ResourcesCompat.getFont(requireContext(), R.font.bree_serif_regular)

        rootView.findViewById<TextView>(R.id.productCount1).typeface = customFont
        rootView.findViewById<TextView>(R.id.productCount2).typeface = customFont
        rootView.findViewById<TextView>(R.id.productCount3).typeface = customFont

        saveProductDataToSharedPreferences(rootView)
        loadProductCounts(rootView)

        rootView.findViewById<ImageButton>(R.id.addProductButton)?.setOnClickListener {
            updateProductCount(1, rootView, 1)
        }

        rootView.findViewById<ImageButton>(R.id.removeProductButton)?.setOnClickListener {
            updateProductCount(-1, rootView, 1)
        }

        rootView.findViewById<ImageButton>(R.id.addProductButton2)?.setOnClickListener {
            updateProductCount(1, rootView, 2)
        }

        rootView.findViewById<ImageButton>(R.id.removeProductButton2)?.setOnClickListener {
            updateProductCount(-1, rootView, 2)
        }

        rootView.findViewById<ImageButton>(R.id.addProductButton3)?.setOnClickListener {
            updateProductCount(1, rootView, 3)
        }

        rootView.findViewById<ImageButton>(R.id.removeProductButton3)?.setOnClickListener {
            updateProductCount(-1, rootView, 3)
        }

        return rootView
    }

    private fun saveProductDataToSharedPreferences(rootView: View) {
        val title1 = rootView.findViewById<TextView>(R.id.productTitle_1_Beleg).text.toString()
        val title2 = rootView.findViewById<TextView>(R.id.productTitle_2_Beleg).text.toString()
        val title3 = rootView.findViewById<TextView>(R.id.productTitle_3_Beleg).text.toString()

        val description1 = rootView.findViewById<TextView>(R.id.productDescription_1_Beleg).text.toString()
        val description2 = rootView.findViewById<TextView>(R.id.productDescription_2_Beleg).text.toString()
        val description3 = rootView.findViewById<TextView>(R.id.productDescription_1_Beleg).text.toString()

        sharedPreferences.edit().apply {
            putString("belegdebroodjes_1_title", title1)
            putString("belegdebroodjes_2_title", title2)
            putString("belegdebroodjes_3_title", title3)

            putString("belegdebroodjes_1_description", description1)
            putString("belegdebroodjes_2_description", description2)
            putString("belegdebroodjes_3_description", description3)

            putInt("belegdebroodjes_1_image", productImages["product1"] ?: 0)
            putInt("belegdebroodjes_2_image", productImages["product2"] ?: 0)
            putInt("belegdebroodjes_3_image", productImages["product3"] ?: 0)

            apply()
        }
    }

    private fun updateProductCount(quantity: Int, rootView: View, productIndex: Int) {
        val productCountTextView = when (productIndex) {
            1 -> rootView.findViewById<TextView>(R.id.productCount1)
            2 -> rootView.findViewById<TextView>(R.id.productCount2)
            3 -> rootView.findViewById<TextView>(R.id.productCount3)
            else -> return
        }

        val uniqueProductId = "belegdebroodjes_$productIndex"
        val currentCount = sharedPreferences.getInt(uniqueProductId, 0)
        val newCount = currentCount + quantity

        if (newCount >= 0) {
            sharedPreferences.edit().putInt(uniqueProductId, newCount).apply()

            val priceDifference = (quantity * (productPrices["product$productIndex"] ?: 0.0)).toFloat()
            val totalPrice = sharedPreferences.getFloat("globalTotalPrice", 0f) + priceDifference
            val totalProducts = sharedPreferences.getInt("globalTotalProducts", 0) + quantity

            sharedPreferences.edit()
                .putFloat("globalTotalPrice", totalPrice)
                .putInt("globalTotalProducts", totalProducts)
                .apply()

            productCountTextView.text = if (newCount > 0) "$newCount" else ""

            (activity as? HomeActivity)?.updateUI()
        }
    }

    private fun loadProductCounts(rootView: View) {
        productIds.forEachIndexed { index, _ ->
            val productCountTextView = when (index + 1) {
                1 -> rootView.findViewById<TextView>(R.id.productCount1)
                2 -> rootView.findViewById<TextView>(R.id.productCount2)
                3 -> rootView.findViewById<TextView>(R.id.productCount3)
                else -> return
            }

            val uniqueProductId = "belegdebroodjes_${index + 1}"
            val savedCount = sharedPreferences.getInt(uniqueProductId, 0)
            productCountTextView.text = if (savedCount > 0) "$savedCount" else ""
        }
    }
}
