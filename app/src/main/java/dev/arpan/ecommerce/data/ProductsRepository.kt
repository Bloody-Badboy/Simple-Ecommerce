package dev.arpan.ecommerce.data

import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterOptions
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.data.source.local.ProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

private const val SIMULATE_NETWORK_DELAY = true

interface ProductsRepository {

    val cartItemCountFlow: Flow<Int>

    val sortByOrderFlow: Flow<SortBy>

    var sortBy: SortBy

    suspend fun getCategories(): ResultWrapper<List<ProductCategory>>

    suspend fun getProducts(category: String, filterOptions: FilterOptions): ResultWrapper<List<ProductItem>>

    suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>>

    suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails>

    suspend fun getFiltersForCategory(category: String): ResultWrapper<List<Filter>>

    suspend fun addProductToCart(addToCart: AddToCart): Boolean

    suspend fun removeProductFromCart(productId: Long): Boolean

    suspend fun isProductInCart(productId: Long): Boolean

    suspend fun getCartProducts(): ResultWrapper<List<CartItem>>
}

class DefaultProductsRepository(
    private val localDataSource: ProductsLocalDataSource,
    private val remoteDataSource: ProductsRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    private var cartItemCount: Int by Delegates.observable(0) { _, _, newValue ->
        cartItemCountChannel.offer(newValue)
    }

    override var sortBy: SortBy by Delegates.observable(SortBy.POPULARITY) { _, _, newValue ->
        sortByChannel.offer(newValue)
    }

    private val cartItemCountChannel: ConflatedBroadcastChannel<Int> by lazy {
        ConflatedBroadcastChannel<Int>().also { channel ->
            channel.offer(cartItemCount)
        }
    }

    private val sortByChannel: ConflatedBroadcastChannel<SortBy> by lazy {
        ConflatedBroadcastChannel<SortBy>().also { channel ->
            channel.offer(sortBy)
        }
    }

    private val cartProducts = mutableSetOf<AddToCart>()

    override val cartItemCountFlow: Flow<Int>
        get() = cartItemCountChannel.asFlow()

    override val sortByOrderFlow: Flow<SortBy>
        get() = sortByChannel.asFlow()

    override suspend fun getCategories(): ResultWrapper<List<ProductCategory>> {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getCategories()
            )
        }
    }

    override suspend fun getProducts(
        category: String,
        filterOptions: FilterOptions
    ): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getProducts(
                    category, filterOptions
                )
            )
        }
    }

    override suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            ResultWrapper.Success(
                remoteDataSource.searchProduct(query)
            )
        }
    }

    override suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails> {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
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
                if (SIMULATE_NETWORK_DELAY)
                    delay(1000)
                val filters = remoteDataSource.getFiltersForCategory(category)
                localDataSource.cachedFilterListForCategory(category, filters)
                ResultWrapper.Success(filters)
            }
        }
    }

    override suspend fun addProductToCart(addToCart: AddToCart): Boolean {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            if (cartProducts.add(addToCart)) {
                cartItemCount++
            }
            true
        }
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            if (cartProducts.removeAll { it.productId == productId }) {
                cartItemCount--
            }
            true
        }
    }

    override suspend fun isProductInCart(productId: Long): Boolean {
        return cartProducts.find { it.productId == productId } != null
    }

    override suspend fun getCartProducts(): ResultWrapper<List<CartItem>> {
        return withContext(dispatcher) {
            if (SIMULATE_NETWORK_DELAY)
                delay(1000)
            ResultWrapper.Success(cartProducts.map {
                val details = remoteDataSource.getProductDetails(it.productId)
                return@map CartItem(
                    productId = it.productId,
                    productName = details.productName,
                    price = details.price,
                    mrp = details.mrp,
                    discount = details.discount,
                    imageUrl = details.imageUrls[0],
                    selectedSize = it.selectedSize,
                    inStoke = details.inStoke
                )
            })
        }
    }
}
