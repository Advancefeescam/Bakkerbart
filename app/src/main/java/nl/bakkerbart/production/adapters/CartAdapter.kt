package nl.bakkerbart.production

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CartItem(
    val title: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val imageName: Int
)

class CartAdapter(private val context: Context, private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.itemImage)
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        val imageResId = when (cartItem.imageName) {
            1 -> sharedPreferences.getInt("panini_1_image", R.drawable.extension_icon)
            2 -> sharedPreferences.getInt("panini_2_image", R.drawable.extension_icon)
            3 -> sharedPreferences.getInt("panini_3_image", R.drawable.extension_icon)
            else -> R.drawable.extension_icon
        }

        holder.productImage.setImageResource(imageResId)
        holder.productTitle.text = cartItem.title
        holder.productDescription.text = cartItem.description
        holder.productPrice.text = "€ ${"%.2f".format(cartItem.price)}"
        holder.productQuantity.text = "x${cartItem.quantity}"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
