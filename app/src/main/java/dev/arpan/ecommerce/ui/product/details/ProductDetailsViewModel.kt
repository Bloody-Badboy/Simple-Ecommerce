package dev.arpan.ecommerce.ui.product.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch

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

    val cartItemCount = productsRepository.cartItemCount

    fun fetchProductDetails(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productsRepository.getProductDetails(productId)
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

    fun addItemToCart(productId: Long) {
        viewModelScope.launch {
            productsRepository.addProductToCart(productId)
        }
    }
}
