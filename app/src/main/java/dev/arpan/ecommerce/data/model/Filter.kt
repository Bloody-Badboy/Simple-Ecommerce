package dev.arpan.ecommerce.data.model

data class FilterNameValue(
    val name: String,
    val value: String
)

sealed class FilterType {
    data class SingleChoice(val options: List<FilterNameValue>) : FilterType()
    data class MultipleChoice(val options: List<FilterNameValue>) : FilterType()
}

data class Filter(val filterNameValue: FilterNameValue, val filterType: FilterType)