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

data class ProductItem(
    val productId: Long,
    val productName: String,
    val price: String,
    val mrp: String,
    val discount: String,
    val category: String,
    val imageUrl: String,
    var tag: String? = null,
    var isWishListed: Boolean,
    val inStoke: Boolean
)
