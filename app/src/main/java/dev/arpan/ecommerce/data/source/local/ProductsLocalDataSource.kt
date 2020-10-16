/*
 * Copyright 2020 Arpan Sarkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
