package dev.arpan.ecommerce.data

import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
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

interface ProductsRepository {

    val cartItemCountFlow: Flow<Int>

    suspend fun getCategories(): ResultWrapper<List<ProductCategory>>

    suspend fun getProducts(category: String): ResultWrapper<List<ProductItem>>

    suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>>

    suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails>

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
    override val cartItemCountFlow: Flow<Int>
        get() = cartItemCountChannel.asFlow()

    private val cartItemCountChannel: ConflatedBroadcastChannel<Int> by lazy {
        ConflatedBroadcastChannel<Int>().also { channel ->
            channel.offer(cartItemCount)
        }
    }

    private val cartProducts = mutableSetOf<AddToCart>()

    override suspend fun getCategories(): ResultWrapper<List<ProductCategory>> {
        return withContext(dispatcher) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getCategories()
            )
        }
    }

    override suspend fun getProducts(category: String): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getProducts(
                    category
                )
            )
        }
    }

    override suspend fun searchProduct(query: String): ResultWrapper<List<ProductItem>> {
        return withContext(dispatcher) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.searchProduct(query)
            )
        }
    }

    override suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails> {
        return withContext(dispatcher) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getProductDetails(productId)
            )
        }
    }

    override suspend fun addProductToCart(addToCart: AddToCart): Boolean {
        return withContext(dispatcher) {
            delay(1000)
            if (cartProducts.add(addToCart)) {
                cartItemCount++
            }
            true
        }
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        return withContext(dispatcher) {
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
