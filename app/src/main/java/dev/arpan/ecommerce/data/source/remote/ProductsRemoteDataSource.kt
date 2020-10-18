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

package dev.arpan.ecommerce.data.source.remote

import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterName
import dev.arpan.ecommerce.data.model.FilterOption
import dev.arpan.ecommerce.data.model.FilterType
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.model.SortBy
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

private const val SIMULATE_NETWORK_DELAY = true

interface ProductsRemoteDataSource {

    suspend fun getCategories(): List<ProductCategory>

    suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): List<ProductItem>

    suspend fun searchProduct(query: String): List<ProductItem>

    suspend fun getProductDetails(productId: Long): ProductDetails

    suspend fun getFiltersForCategory(category: String): List<Filter>

    suspend fun getCartProducts(): List<CartItem>

    suspend fun isProductInCart(productId: Long): Boolean

    suspend fun addProductToCart(addToCart: AddToCart): Boolean

    suspend fun removeProductFromCart(productId: Long): Boolean
}

class DefaultProductsRemoteDataSource() : ProductsRemoteDataSource {

    private val mockProducts = mutableListOf<ProductInfo>()
    private val mockCategories = listOf(
        ProductCategory(name = "Top Wear", "top_wear"),
        ProductCategory(name = "Bottom Wear", "bottom_wear"),
        ProductCategory(name = "Jewellery", "jewellery")
    )

    data class ProductInfo(
        val productId: Long,
        val productName: String,
        val price: String,
        val mrp: String,
        val discount: String,
        val category: String,
        val imageUrls: List<String> = emptyList(),
        var tag: String? = null,
        var isWishListed: Boolean,
        val inStoke: Boolean,
        val rating: Double,
        val availableSize: List<String> = emptyList()
    ) {
        fun toProductItem() = ProductItem(
            productId = productId,
            productName = productName,
            price = price,
            mrp = mrp,
            discount = discount,
            imageUrl = imageUrls[0],
            category = category,
            tag = tag,
            isWishListed = isWishListed,
            inStoke = inStoke
        )

        fun toProductDetails(suggested: List<ProductItem>) = ProductDetails(
            productId = productId,
            productName = productName,
            price = price,
            mrp = mrp,
            discount = discount,
            imageUrls = imageUrls,
            isWishListed = isWishListed,
            inStoke = inStoke,
            rating = rating,
            availableSize = availableSize,
            suggestedProducts = suggested
        )

        fun toCartItem(selectedSize: String?) = CartItem(
            productId = productId,
            productName = productName,
            price = price,
            mrp = mrp,
            discount = discount,
            imageUrl = imageUrls[0],
            selectedSize = selectedSize,
            inStoke = inStoke
        )
    }

    private val cartProducts = mutableSetOf<AddToCart>()

    init {
        mockProducts.apply {
            for (i in 0 until 100) {
                val categoryIndex = Random.nextInt(mockCategories.size)
                val inStock = Random.nextBoolean()
                val mrp = Random.nextInt(100, 1000)
                val discount = max(10, Random.nextInt(50))
                val allSizes = listOf(
                    "S",
                    "M",
                    "L",
                    "XL",
                    "2XL"
                )
                add(
                    ProductInfo(
                        productId = i.toLong(),
                        productName = "Product ${mockCategories[categoryIndex].name} #${i + 1}",
                        price = (mrp - mrp * (discount / 100f)).roundToInt().toString(),
                        mrp = mrp.toString(),
                        discount = discount.toString(),
                        imageUrls = listOf(
                            "https://picsum.photos/seed/$i/480/640?random=$i",
                            "https://picsum.photos/seed/${i + 1}/480/640?random=${i + 1}",
                            "https://picsum.photos/seed/${i + 2}/480/640?random=${i + 2}",
                            "https://picsum.photos/seed/${i + 3}/480/640?random=${i + 3}}"
                        ),
                        category = mockCategories[categoryIndex].value,
                        tag = if (Random.nextBoolean() && inStock) "New Arrival" else null,
                        isWishListed = Random.nextBoolean(),
                        inStoke = inStock,
                        rating = Random.nextDouble(1.toDouble(), 5.toDouble()),
                        availableSize = if (mockCategories[categoryIndex].value == "jewellery") emptyList() else allSizes.subList(
                            0,
                            Random.nextInt(1, allSizes.size)
                        )
                    )
                )
            }
        }
    }

    override suspend fun getCategories(): List<ProductCategory> {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return mockCategories
    }

    override suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): List<ProductItem> {
        if (SIMULATE_NETWORK_DELAY) delay(1000)

        var products = mockProducts.filter { it.category == category }

        var includeOutOfStock = false
        selectedFilterOptions.filterMap.entries.forEach { entry ->
            if (entry.key.value == "discount") {
                if (entry.value.isNotEmpty()) {
                    val discount = entry.value[0].value.toIntOrNull() ?: -1
                    if (discount >= 0) {
                        products = products.filter { it.discount.toInt() >= discount }
                    }
                }
            }
            if (entry.key.value == "price") {
                if (entry.value.isNotEmpty()) {
                    val splits = entry.value[0].value.split("-")
                    products = if (splits.size >= 2) {
                        val lower = splits[0].toInt()
                        val higher = splits[1].toInt()
                        products.filter { productItem -> productItem.price.toInt() in lower..higher }
                    } else {
                        val minVal = entry.value[0].value.toInt()
                        products.filter { productItem -> productItem.price.toInt() >= minVal }
                    }
                }
            }
            if (entry.key.value == "size") {
                if (entry.value.isNotEmpty()) {
                    products = products.filter {
                        it.availableSize.any { size ->
                            entry.value.map { option -> option.value }
                                .contains(size.toLowerCase(Locale.getDefault()))
                        }
                    }
                }
            }
            if (entry.key.value == "availability") {
                if (entry.value.isNotEmpty()) {
                    includeOutOfStock = true
                }
            }
        }

        return when (selectedFilterOptions.sortBy) {
            SortBy.PRICE_HIGH_TO_LOW -> {
                products.sortedByDescending { it.price }
            }
            SortBy.PRICE_LOW_HIGH -> {
                products.sortedBy { it.price }
            }
            else -> {
                products
            }
        }.filter {
            if (includeOutOfStock) {
                true
            } else {
                it.inStoke
            }
        }.map {
            it.toProductItem()
        }
    }

    override suspend fun searchProduct(query: String): List<ProductItem> {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return mockProducts.filter { it.productName.contains(query) }.map {
            it.toProductItem()
        }
    }

    override suspend fun getProductDetails(productId: Long): ProductDetails {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        val product = mockProducts.first { it.productId == productId }
        return product.toProductDetails(
            suggested = mockProducts.filter { it.category == product.category && it.productId != productId }
                .take(8).map { it.toProductItem() }
        )
    }

    override suspend fun getFiltersForCategory(category: String): List<Filter> {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        val filterOptionSize = Filter(
            filterName = FilterName(
                name = "Size",
                value = "size"
            ),
            filterType = FilterType.MultipleChoice(
                options = mutableListOf<FilterOption>().apply {
                    add(FilterOption(name = "S", value = "s"))
                    add(FilterOption(name = "M", value = "m"))
                    add(FilterOption(name = "L", value = "l"))
                    add(FilterOption(name = "XL", value = "xl"))
                    add(FilterOption(name = "2XL", value = "xxl"))
                }
            )
        )

        val filterRating = Filter(
            filterName = FilterName(
                name = "Rating",
                value = "rating"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "4.5 or higher", value = "4.5"),
                    FilterOption(name = "3.5 or higher", value = "3.5"),
                    FilterOption(name = "2.5 or higher", value = "2.5"),
                    FilterOption(name = "1.5 or below", value = "1.5"),
                )
            )
        )

        val filterOptionDiscount = Filter(
            filterName = FilterName(
                name = "Discount",
                value = "discount"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "40% or more", value = "40"),
                    FilterOption(name = "30% or more", value = "30"),
                    FilterOption(name = "20% or more", value = "20"),
                    FilterOption(name = "10% or more", value = "10"),
                    FilterOption(name = "10% or below", value = "0")
                )
            )
        )

        val filterOptionPrice = Filter(
            filterName = FilterName(
                name = "Price",
                value = "price"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "₹100 or below", value = "0-100"),
                    FilterOption(name = "₹101 - ₹500", value = "101-200"),
                    FilterOption(name = "₹501 - ₹1000", value = "101-200"),
                    FilterOption(name = "₹1000 or above", value = "1000")
                )
            )
        )

        val filterOptionAvailability = Filter(
            filterName = FilterName(
                name = "Availability ",
                value = "availability"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "Include Out Of Stock", value = "oos")
                )
            )
        )

        if (category == "jewellery") {
            return listOf(
                filterRating,
                filterOptionDiscount,
                filterOptionPrice,
                filterOptionAvailability
            )
        } else {
            return listOf(
                filterOptionSize,
                filterRating,
                filterOptionDiscount,
                filterOptionPrice,
                filterOptionAvailability
            )
        }
    }

    override suspend fun getCartProducts(): List<CartItem> {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return cartProducts.map {
            mockProducts.find { info -> info.productId == it.productId }.let { productInfo ->
                return@map productInfo!!.toCartItem(it.selectedSize)
            }
        }
    }

    override suspend fun isProductInCart(productId: Long): Boolean {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return cartProducts.find { it.productId == productId } != null
    }

    override suspend fun addProductToCart(addToCart: AddToCart): Boolean {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return cartProducts.add(addToCart)
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        if (SIMULATE_NETWORK_DELAY) delay(1000)
        return cartProducts.removeAll { it.productId == productId }
    }
}
