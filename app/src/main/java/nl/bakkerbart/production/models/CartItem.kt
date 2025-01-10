package nl.bakkerbart.production.models


data class CartItem(
    val title: String,
    val description: String,
    val price: Double,
    var quantity: Int
)
