package dev.arpan.ecommerce.ui.cart

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch

class CartViewModel @ViewModelInject constructor(private val repository: ProductsRepository) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _products = MutableLiveData<List<CartItem>>()
    val products: LiveData<List<CartItem>>
        get() = _products

    private val _priceDetails = MutableLiveData<CartPriceDetails>()
    val priceDetails: LiveData<CartPriceDetails>
        get() = _priceDetails

    val isCartEmpty: LiveData<Boolean> = Transformations.map(products) { it.isEmpty() }

    fun fetchCartProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getCartProducts()
            _isLoading.value = false
            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _priceDetails.value = calculateCartPriceDetails(response.value)
                    _products.value = response.value
                }
            }
        }
    }

    fun removeProductFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeProductFromCart(productId)

            _products.value?.filter { it.productId != productId }?.run {
                _priceDetails.value = calculateCartPriceDetails(this)
                _products.value = this
            }
        }
    }

    private fun calculateCartPriceDetails(cartProducts: List<CartItem>): CartPriceDetails {
        val totalMrp = cartProducts.sumBy { it.mrp.toInt() }
        val totalPrice = cartProducts.sumBy { it.price.toInt() }
        return CartPriceDetails(
            productCount = cartProducts.size,
            totalMrp = totalMrp,
            totalDiscount = totalMrp - totalPrice,
            totalPrice = totalPrice
        )
    }
}

data class CartPriceDetails(
    val productCount: Int,
    val totalMrp: Int,
    val totalDiscount: Int,
    val totalPrice: Int
)