package dev.arpan.ecommerce.ui.product.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch

class ProductListViewModel @ViewModelInject constructor(private val repository: ProductsRepository) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>>
        get() = _products

    fun fetchProducts(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getProducts(category)
            _isLoading.value = false
            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _products.value = response.value
                }
            }
        }
    }
}
