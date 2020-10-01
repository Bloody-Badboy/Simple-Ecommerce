package dev.arpan.ecommerce.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.source.local.ProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface ProductsRepository {

    val cartItemCount: LiveData<Int>

    suspend fun getCategories(): ResultWrapper<List<ProductCategory>>

    suspend fun getProducts(category: String): ResultWrapper<List<ProductItem>>

    suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails>

    suspend fun addProductToCart(productId: Long): Boolean

    suspend fun removeProductFromCart(productId: Long): Boolean

    suspend fun getCartProducts(): ResultWrapper<List<CartItem>>
}

class DefaultProductsRepository(
    private val localDataSource: ProductsLocalDataSource,
    private val remoteDataSource: ProductsRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    private val _cartItemCount = MutableLiveData(0)
    override val cartItemCount: LiveData<Int>
        get() = _cartItemCount

    private val cartProductIds = mutableSetOf<Long>()

    override suspend fun getCategories(): ResultWrapper<List<ProductCategory>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getCategories()
            )
        }
    }

    override suspend fun getProducts(category: String): ResultWrapper<List<ProductItem>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getProducts(
                    category
                )
            )
        }
    }

    override suspend fun getProductDetails(productId: Long): ResultWrapper<ProductDetails> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            ResultWrapper.Success(
                remoteDataSource.getProductDetails(productId)
            )
        }
    }

    override suspend fun addProductToCart(productId: Long): Boolean {
        if (cartProductIds.add(productId))
            _cartItemCount.value = _cartItemCount.value?.inc()
        return true
    }

    override suspend fun removeProductFromCart(productId: Long): Boolean {
        if (cartProductIds.remove(productId))
            _cartItemCount.value = _cartItemCount.value?.dec()
        return true
    }

    override suspend fun getCartProducts(): ResultWrapper<List<CartItem>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            ResultWrapper.Success(cartProductIds.map {
                val details = remoteDataSource.getProductDetails(it)
                return@map CartItem(
                    productId = it,
                    productName = details.productName,
                    price = details.price,
                    mrp = details.mrp,
                    discount = details.discount,
                    imageUrl = details.imageUrls[0],
                    inStoke = details.inStoke
                )
            })
        }
    }
}
