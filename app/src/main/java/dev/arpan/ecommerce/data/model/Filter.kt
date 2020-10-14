package dev.arpan.ecommerce.data.model

typealias AppliedFilterMap = Map<FilterName, List<FilterOption>>

enum class SortBy(val displayName: String) {
    POPULARITY("Popularity"),
    PRICE_HIGH_TO_LOW("Price - High to Low"),
    PRICE_LOW_HIGH("Price - Low to High");

    companion object {
        fun default(): SortBy {
            return POPULARITY
        }
    }
}

data class FilterOption(
    val name: String,
    val value: String
)

data class FilterName(
    val name: String,
    val value: String
)

sealed class FilterType {
    data class SingleChoice(val options: List<FilterOption>) : FilterType()
    data class MultipleChoice(val options: List<FilterOption>) : FilterType()
}

data class Filter(val filterName: FilterName, val filterType: FilterType)