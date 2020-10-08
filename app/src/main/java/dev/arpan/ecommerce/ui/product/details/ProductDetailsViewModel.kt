package dev.arpan.ecommerce.ui.product.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.result.Event
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository
) :
    ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _productDetail = MutableLiveData<ProductDetails>()
    val productDetails: LiveData<ProductDetails>
        get() = _productDetail

    private val _addToCartState = MutableLiveData<Event<AddToCartState>>()
    val addToCartState: LiveData<Event<AddToCartState>>
        get() = _addToCartState

    private val _isProductInCart = MutableLiveData<Boolean>()
    val isProductInCart: LiveData<Boolean>
        get() = _isProductInCart

    val cartItemCount = productsRepository.cartItemCount

    fun fetchProductDetails(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            val response = productsRepository.getProductDetails(productId)
            _isProductInCart.value = productsRepository.isProductInCart(productId)

            _isLoading.value = false

            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _productDetail.value = response.value
                }
            }
        }
    }

    fun addItemToCart(productId: Long, selectedSize: String?) {
        viewModelScope.launch {
            val addToCart = AddToCart(
                productId = productId,
                selectedSize = selectedSize
            )

            _addToCartState.value = Event(AddToCartState.Adding)
            withContext(Dispatchers.IO) {
                delay(3000L)
            }
            productsRepository.addProductToCart(addToCart)
            _addToCartState.value = Event(AddToCartState.Added)
        }
    }
}

sealed class AddToCartState {
    object Adding : AddToCartState()
    object Added : AddToCartState()
}
