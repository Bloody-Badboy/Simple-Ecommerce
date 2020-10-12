package dev.arpan.ecommerce.data.source.local

import dev.arpan.ecommerce.data.model.Filter

interface ProductsLocalDataSource {
    fun getCachedFilterListForCategory(category: String): List<Filter>?

    fun cachedFilterListForCategory(category: String, filters: List<Filter>)
}

class DefaultProductsLocalDataSource : ProductsLocalDataSource {
    private val categoryFilterMap = mutableMapOf<String, List<Filter>>()

    override fun getCachedFilterListForCategory(category: String): List<Filter>? {
        return categoryFilterMap[category]
    }

    override fun cachedFilterListForCategory(category: String, filters: List<Filter>) {
        categoryFilterMap[category] = filters
    }
}
