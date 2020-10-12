package dev.arpan.ecommerce.data.model

enum class SortBy(val displayName: String) {
    POPULARITY("Popularity"),
    PRICE_HIGH_TO_LOW("Price - High to Low"),
    PRICE_LOW_HIGH("Price - Low to High"),
    NEW_ARRIVAL("New Arrival")
}

data class FilterOptions(
    val sortBy: SortBy
)