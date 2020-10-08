package dev.arpan.ecommerce.data.model

data class AddToCart(
    val productId: Long,
    val selectedSize:String?
)