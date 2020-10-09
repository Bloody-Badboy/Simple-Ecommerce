package dev.arpan.ecommerce.data.model

data class ProductDetails(
    val productId: Long,
    val productName: String,
    val price: String,
    val mrp: String,
    val discount: String,
    val imageUrls: List<String> = emptyList(),
    var isWishlisted: Boolean,
    val inStoke: Boolean,
    val rating: Double,
    val availableSize: List<String> = emptyList(),
    val suggestedProducts :List<ProductItem> = emptyList()
) {
    val hasImages = imageUrls.isNotEmpty()

    val sizeOptionAvailable = availableSize.isNotEmpty()
}
