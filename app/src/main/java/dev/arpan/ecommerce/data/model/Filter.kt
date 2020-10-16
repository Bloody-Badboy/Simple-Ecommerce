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
