package dev.arpan.ecommerce.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val categoryFilterMap = mutableMapOf<String, List<Filter>>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _productCategories = MutableLiveData<List<ProductCategory>>()

    val productCategories: LiveData<List<ProductCategory>>
        get() = _productCategories

    val cartItemCount = productsRepository.cartItemCountFlow.asLiveData()

    private val _filters: MutableList<Filter> = mutableListOf()
    val filters: MutableList<Filter>
        get() = _filters

    var currentPageIndex: Int = -1

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

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

    fun fetchCategoryFilters(category: String) {
        viewModelScope.launch {
            categoryFilterMap[category]?.let {
                _filters.apply {
                    clear()
                    addAll(it)
                }
            } ?: kotlin.run {
                when (val categoryFilterResponse =
                    productsRepository.getFiltersForCategory(category)) {
                    is ResultWrapper.NetworkError -> {
                    }
                    is ResultWrapper.GenericError -> {
                    }
                    is ResultWrapper.Success -> {
                        categoryFilterMap[category] = categoryFilterResponse.value
                        _filters.apply {
                            clear()
                            addAll(categoryFilterResponse.value)
                        }
                    }
                }
            }
        }
    }
}
