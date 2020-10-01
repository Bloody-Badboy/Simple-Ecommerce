package dev.arpan.ecommerce.data.model

data class ProductItem(
    val productId: Long,
    val productName: String,
    val price: String,
    val mrp: String,
    val discount: String,
    val category: String,
    val imageUrl: String,
    var tag: String? = null,
    var isWishlisted: Boolean,
    val inStoke: Boolean
)
