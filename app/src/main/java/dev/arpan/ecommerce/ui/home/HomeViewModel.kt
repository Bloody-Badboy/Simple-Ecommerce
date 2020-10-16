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

package dev.arpan.ecommerce.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _productCategories = MutableLiveData<List<ProductCategory>>()

    val productCategories: LiveData<List<ProductCategory>>
        get() = _productCategories

    val cartItemCount = productsRepository.cartItemCountFlow.asLiveData()

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

    fun getSortByOrder(category: String): SortBy {
        return productsRepository.getSelectedSortByForCategory(category)
    }

    fun setSortByOrder(category: String, sortBy: SortBy) {
        productsRepository.setSelectedSortByForCategory(category, sortBy)
    }
}
