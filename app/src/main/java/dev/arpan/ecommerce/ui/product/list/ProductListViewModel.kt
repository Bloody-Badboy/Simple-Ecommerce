package dev.arpan.ecommerce.ui.product.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AppliedFilterMap
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListViewModel @ViewModelInject constructor(private val repository: ProductsRepository) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>>
        get() = _products

    val isEmptyList: LiveData<Boolean> = Transformations.map(_products) {
        it.isNullOrEmpty()
    }

    var previousSortByOrder: SortBy = SortBy.default()
    var previousAppliedFilterMap: AppliedFilterMap = emptyMap()

    val sortBy: LiveData<SortBy>
        get() = repository.sortByOrderFlow.asLiveData()

    private val _appliedFilterMap = MutableLiveData<AppliedFilterMap>()
    val appliedFilterMap: LiveData<AppliedFilterMap>
        get() = _appliedFilterMap

    fun fetchProducts(
        category: String,
        sortBy: SortBy = repository.sortBy,
        filterMap: AppliedFilterMap = repository.getAppliedFilterForCategory(category)
    ) {
        val selectedFilterOptions = SelectedFilterOptions(
            sortBy = sortBy,
            filterMap = filterMap
        )
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getProducts(category, selectedFilterOptions)
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

    fun observeAppliedFilterForCategory(category: String) {
        viewModelScope.launch {
            repository.categoryAppliedFiltersFlow.collectLatest { map ->
                map.iterator().forEach {
                    if (it.key == category) {
                        _appliedFilterMap.value = it.value
                        return@forEach
                    }
                }
            }
        }
    }
}
