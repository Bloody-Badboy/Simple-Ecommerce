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

package dev.arpan.ecommerce.data

import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.AppliedFilterMap
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterName
import dev.arpan.ecommerce.data.model.FilterOption
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.data.source.local.ProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

interface ProductsRepository {

    val cartItemCountFlow: Flow<Int>

    val categorySortByOrderFlow: Flow<Map<String, SortBy>>

    val categoryAppliedFiltersFlow: Flow<Map<String, AppliedFilterMap>>

    suspend fun getCategories(): ResultWrapper<List<ProductCategory>>

    suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): ResultWrapper<List<ProductItem>>

    suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>>

    suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails>

    suspend fun getFiltersForCategory(category: String): ResultWrapper<List<Filter>>

    suspend fun addProductToCart(addToCart: AddToCart): Boolean

    suspend fun removeProductFromCart(productId: Long): Boolean

    suspend fun isProductInCart(productId: Long): Boolean

    suspend fun getCartProducts(): ResultWrapper<List<CartItem>>

    fun setSelectedSortByForCategory(
        category: String,
        sortBy: SortBy
    )

    fun getSelectedSortByForCategory(
        category: String
    ): SortBy

    fun setAppliedFilterForCategory(
        category: String,
        filterMap: AppliedFilterMap
    )

    fun getAppliedFilterForCategory(category: String): AppliedFilterMap
}

class DefaultProductsRepository(
    private val localDataSource: ProductsLocalDataSource,
    private val remoteDataSource: ProductsRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    private var cartItemCount: Int by Delegates.observable(0) { _, _, newValue ->
        cartItemCountChannel.offer(newValue)
    }

    private val cartItemCountChannel: ConflatedBroadcastChannel<Int> by lazy {
        ConflatedBroadcastChannel<Int>().also { channel ->
            channel.offer(cartItemCount)
        }
    }

    private val categorySortByChannel: ConflatedBroadcastChannel<Map<String, SortBy>> by lazy {
        ConflatedBroadcastChannel()
    }

    private val categoryAppliedFiltersChannel: ConflatedBroadcastChannel<Map<String, AppliedFilterMap>> by lazy {
        ConflatedBroadcastChannel()
    }

    private val categorySelectedSortByMap = mutableMapOf<String, SortBy>()
    private val categoryAppliedFilterMap = mutableMapOf<String, AppliedFilterMap>()

    override val cartItemCountFlow: Flow<Int>
        get() = cartItemCountChannel.asFlow()

    override val categorySortByOrderFlow: Flow<Map<String, SortBy>>
        get() = categorySortByChannel.asFlow()

    override val categoryAppliedFiltersFlow: Flow<Map<String, AppliedFilterMap>>
        get() = categoryAppliedFiltersChannel.asFlow()

    override suspend fun getCategories(): ResultWrapper<List<ProductCategory>> {
        return withContext(dispatcher) {
            ResultWrapper.Success(
                remoteDataSource.getCategories()
            )
        }
    }

    override suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            ResultWrapper.Success(
                remoteDataSource.getProducts(
                    category, selectedFilterOptions
                )
            )
        }
    }

    override suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            ResultWrapper.Success(
                remoteDataSource.searchProduct(query)
            )
        }
    }

    override suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails> {
        return withContext(dispatcher) {
            ResultWrapper.Success(
                remoteDataSource.getProductDetails(productId)
            )
        }
    }

    override suspend fun getFiltersForCategory(category: String): ResultWrapper<List<Filter>> {
        val cachedFilters = localDataSource.getCachedFilterListForCategory(category)
        return if (cachedFilters != null) {
            ResultWrapper.Success(cachedFilters)
        } else {
            withContext(dispatcher) {
                val filters = remoteDataSource.getFiltersForCategory(category)
                localDataSource.cachedFilterListForCategory(category, filters)
                ResultWrapper.Success(filters)
            }
        }
    }

    override suspend fun addProductToCart(addToCart: AddToCart): Boolean {
        return withContext(dispatcher) {
            if (remoteDataSource.addProductToCart(addToCart)) {
                cartItemCount++
                true
            } else {
                false
            }
        }
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        return withContext(dispatcher) {
            if (remoteDataSource.removeProductFromCart(productId)) {
                cartItemCount--
                true
            } else {
                false
            }
        }
    }

    override suspend fun isProductInCart(productId: Long): Boolean {
        return remoteDataSource.isProductInCart(productId)
    }

    override suspend fun getCartProducts(): ResultWrapper<List<CartItem>> {
        return withContext(dispatcher) {
            ResultWrapper.Success(
                remoteDataSource.getCartProducts()
            )
        }
    }

    override fun setSelectedSortByForCategory(category: String, sortBy: SortBy) {
        categorySelectedSortByMap[category] = sortBy
        categorySortByChannel.offer(categorySelectedSortByMap)
    }

    override fun getSelectedSortByForCategory(category: String): SortBy {
        return categorySelectedSortByMap[category] ?: SortBy.default()
    }

    override fun setAppliedFilterForCategory(
        category: String,
        filterMap: Map<FilterName, List<FilterOption>>
    ) {
        categoryAppliedFilterMap[category] = filterMap
        categoryAppliedFiltersChannel.offer(categoryAppliedFilterMap)
    }

    override fun getAppliedFilterForCategory(category: String): AppliedFilterMap {
        return categoryAppliedFilterMap[category] ?: emptyMap()
    }
}
