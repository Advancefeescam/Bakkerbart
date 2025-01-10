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

class panini : Fragment() {

    private val productPrices = mapOf(
        "panini_1" to 5.0,
        "panini_2" to 10.0,
        "panini_3" to 15.0
    )

    private val productIds = listOf("panini_1", "panini_2", "panini_3")

    private val productImages = mapOf(
        "product1" to R.drawable.output_onlinepngtools_10_,
        "product2" to R.drawable.output_onlinepngtools_9_,
        "product3" to R.drawable.output_onlinepngtools_11_
    )

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.panini, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val customFont = ResourcesCompat.getFont(requireContext(), R.font.bree_serif_regular)

        rootView.findViewById<TextView>(R.id.paniniMozzarellaCount).typeface = customFont
        rootView.findViewById<TextView>(R.id.dubbeldikkeTostiMozzarellaCount).typeface = customFont
        rootView.findViewById<TextView>(R.id.paniniHamKaasCount).typeface = customFont

        updateUI(rootView)

        rootView.findViewById<ImageButton>(R.id.addPaniniMozzarellaButton)?.setOnClickListener {
            updateProductCount("panini_1", 1, rootView)
        }

        rootView.findViewById<ImageButton>(R.id.removePaniniMozzarellaButton)?.setOnClickListener {
            updateProductCount("panini_1", -1, rootView)
        }

        rootView.findViewById<ImageButton>(R.id.addDubbeldikkeTostiMozzarellaButton)?.setOnClickListener {
            updateProductCount("panini_2", 1, rootView)
        }

        rootView.findViewById<ImageButton>(R.id.removeDubbeldikkeTostiMozzarellaButton)?.setOnClickListener {
            updateProductCount("panini_2", -1, rootView)
        }

        rootView.findViewById<ImageButton>(R.id.addPaniniHamKaasButton)?.setOnClickListener {
            updateProductCount("panini_3", 1, rootView)
        }

        rootView.findViewById<ImageButton>(R.id.removePaniniHamKaasButton)?.setOnClickListener {
            updateProductCount("panini_3", -1, rootView)
        }


        saveProductDataToSharedPreferences(rootView)

        return rootView
    }

    private fun saveProductDataToSharedPreferences(rootView: View) {

        val titlePanini1 = rootView.findViewById<TextView>(R.id.productTitle_1_Panini).text.toString()
        val titlePanini2 = rootView.findViewById<TextView>(R.id.productTitle_2_Panini).text.toString()
        val titlePanini3 = rootView.findViewById<TextView>(R.id.productTitle_3_Panini).text.toString()

        val descriptionPanini1 = rootView.findViewById<TextView>(R.id.productDescription_1_Panini).text.toString()
        val descriptionPanini2 = rootView.findViewById<TextView>(R.id.productDescription_2_Panini).text.toString()
        val descriptionPanini3 = rootView.findViewById<TextView>(R.id.productDescription_3_Panini).text.toString()

        val imagePanini1 = R.drawable.output_onlinepngtools_10_
        val imagePanini2 = R.drawable.output_onlinepngtools_9_
        val imagePanini3 = R.drawable.output_onlinepngtools_11_


        sharedPreferences.edit().apply {
            putString("panini_1_title", titlePanini1)
            putString("panini_2_title", titlePanini2)
            putString("panini_3_title", titlePanini3)

            putString("panini_1_description", descriptionPanini1)
            putString("panini_2_description", descriptionPanini2)
            putString("panini_3_description", descriptionPanini3)

            putInt("panini_1_image", imagePanini1)
            putInt("panini_2_image", imagePanini2)
            putInt("panini_3_image", imagePanini3)

            apply()
        }
    }



    private fun updateProductCount(productId: String, quantity: Int, rootView: View) {
        val productCountTextView = when (productId) {
            "panini_1" -> rootView.findViewById<TextView>(R.id.paniniMozzarellaCount)
            "panini_2" -> rootView.findViewById<TextView>(R.id.dubbeldikkeTostiMozzarellaCount)
            "panini_3" -> rootView.findViewById<TextView>(R.id.paniniHamKaasCount)
            else -> return
        }

        val currentCount = sharedPreferences.getInt(productId, 0)
        val newCount = currentCount + quantity

        if (newCount >= 0) {
            sharedPreferences.edit().putInt(productId, newCount).apply()

            val priceDifference = (quantity * (productPrices[productId] ?: 0.0)).toFloat()
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

    private fun updateUI(rootView: View) {
        productIds.forEach { productId ->
            val productCountTextView = when (productId) {
                "panini_1" -> rootView.findViewById<TextView>(R.id.paniniMozzarellaCount)
                "panini_2" -> rootView.findViewById<TextView>(R.id.dubbeldikkeTostiMozzarellaCount)
                "panini_3" -> rootView.findViewById<TextView>(R.id.paniniHamKaasCount)
                else -> return
            }

            val savedCount = sharedPreferences.getInt(productId, 0)
            productCountTextView.text = if (savedCount > 0) "$savedCount" else ""

        }
    }
}
