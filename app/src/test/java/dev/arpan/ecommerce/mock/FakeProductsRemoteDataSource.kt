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

package dev.arpan.ecommerce.mock

import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource

class FakeProductsRemoteDataSource() : ProductsRemoteDataSource {

    private val mockProducts = mutableListOf<ProductInfo>()
    private val mockCategories = listOf(
        ProductCategory(name = "Top Wear", "top_wear"),
        ProductCategory(name = "Bottom Wear", "bottom_wear")
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
            imageUrl = "",
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
            for (i in 0 until 10) {
                add(
                    ProductInfo(
                        productId = i.toLong(),
                        productName = "Product #${i + 1}",
                        price = "50",
                        mrp = "100",
                        discount = "50",
                        imageUrls = emptyList(),
                        category = if (i >= 5) mockCategories[1].value else mockCategories[0].value,
                        tag = null,
                        isWishListed = false,
                        inStoke = false,
                        rating = 5.0,
                        availableSize = emptyList()
                    )
                )
            }
        }
    }

    override suspend fun getCategories(): List<ProductCategory> {
        return mockCategories
    }

    override suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): List<ProductItem> {
        return mockProducts.filter { it.category == category }.map {
            it.toProductItem()
        }
    }

    override suspend fun searchProduct(query: String): List<ProductItem> {
        return mockProducts.filter { it.productName.contains(query) }.map {
            it.toProductItem()
        }
    }

    override suspend fun getProductDetails(productId: Long): ProductDetails {
        val product = mockProducts.first { it.productId == productId }
        return product.toProductDetails(
            suggested = mockProducts.filter { it.category == product.category && it.productId != productId }
                .take(8).map { it.toProductItem() }
        )
    }

    override suspend fun getFiltersForCategory(category: String): List<Filter> {
        return emptyList()
    }

    override suspend fun getCartProducts(): List<CartItem> {
        return cartProducts.map {
            mockProducts.find { info -> info.productId == it.productId }.let { productInfo ->
                return@map productInfo!!.toCartItem(it.selectedSize)
            }
        }
    }

    override suspend fun isProductInCart(productId: Long): Boolean {
        return cartProducts.find { it.productId == productId } != null
    }

    override suspend fun addProductToCart(addToCart: AddToCart): Boolean {
        return cartProducts.add(addToCart)
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        return cartProducts.removeAll { it.productId == productId }
    }
}
