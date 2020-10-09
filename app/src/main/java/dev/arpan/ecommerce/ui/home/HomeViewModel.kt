package dev.arpan.ecommerce.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository
) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _productCategories = MutableLiveData<List<ProductCategory>>()
    val productCategories: LiveData<List<ProductCategory>>
        get() = _productCategories

    val cartItemCount = productsRepository.cartItemCountFlow.asLiveData()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productsRepository.getCategories()
            _isLoading.value = false
            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _productCategories.value = response.value
                }
            }
        }
    }
}
