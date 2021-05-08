/*
 * Copyright 2020 Arpan Sarkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arpan.ecommerce.ui.product.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AppliedFilterMap
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(private val repository: ProductsRepository) :
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

    private val _sortBy = MutableLiveData<SortBy>()
    val sortBy: LiveData<SortBy>
        get() = _sortBy

    private val _appliedFilterMap = MutableLiveData<AppliedFilterMap>()
    val appliedFilterMap: LiveData<AppliedFilterMap>
        get() = _appliedFilterMap

    fun fetchProducts(
        category: String,
        sortBy: SortBy = repository.getSelectedSortByForCategory(category),
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

    fun observeChangesForCategory(category: String) {

        viewModelScope.launch {
            repository.categorySortByOrderFlow.collectLatest { map ->
                map.entries.forEach {
                    if (it.key == category) {
                        Timber.d("Sort Changed!")
                        _sortBy.value = it.value
                        return@forEach
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.categoryAppliedFiltersFlow.collect { map ->
                map.entries.forEach {
                    if (it.key == category) {
                        _appliedFilterMap.value = it.value
                        Timber.d("Filter Changed!")
                        return@forEach
                    }
                }
            }

            /*repository.categorySortByOrderFlow.collectLatest { map ->
                map.entries.forEach {

                }
            }*/
        }
    }
}
