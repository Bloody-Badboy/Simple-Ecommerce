package dev.arpan.ecommerce.data.model

data class CartItem(
    val productId: Long,
    val productName: String,
    val price: String,
    val mrp: String,
    val discount: String,
    val imageUrl: String,
    val selectedSize: String?,
    val inStoke: Boolean
)
