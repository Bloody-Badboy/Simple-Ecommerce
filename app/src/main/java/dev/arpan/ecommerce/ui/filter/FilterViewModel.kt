package dev.arpan.ecommerce.ui.filter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AppliedFilterMap
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch

class FilterViewModel @ViewModelInject constructor(private val repository: ProductsRepository) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _filters = MutableLiveData<List<Filter>>()
    val filters: MutableLiveData<List<Filter>>
        get() = _filters

    fun fetchCategoryFilters(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val categoryFilterResponse = repository.getFiltersForCategory(category)
            _isLoading.value = false
            when (categoryFilterResponse) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _filters.value = categoryFilterResponse.value
                }
            }
        }
    }

    fun setAppliedFilterForCategory(
        category: String,
        filterMap: AppliedFilterMap
    ) = repository.setAppliedFilterForCategory(category, filterMap)

    fun getAppliedFilterForCategory(category: String) =
        repository.getAppliedFilterForCategory(category)
}