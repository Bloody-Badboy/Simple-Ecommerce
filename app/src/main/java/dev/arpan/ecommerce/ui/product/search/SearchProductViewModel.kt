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

package dev.arpan.ecommerce.ui.product.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchProductViewModel @Inject constructor(private val repository: ProductsRepository) :
    ViewModel() {
    companion object {
        const val QUERY_MIN_LENGTH = 2
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>>
        get() = _products

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    private var previousSearchQuery: String? = null
    private var searchJob: Job? = null

    private fun searchProducts(query: String) {
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            val response = repository.searchProduct(query)
            _isLoading.value = false
            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _products.value = response.value
                    _isEmptyList.value = response.value.isEmpty()
                }
            }
        }
    }

    fun onSearchQueryChanged(searchQuery: String) {
        if (searchQuery.length < QUERY_MIN_LENGTH) {
            // Hide the no results view if query less than required length
            _isEmptyList.value = false
            _products.value = emptyList()
            return
        }
        if (previousSearchQuery != searchQuery) {
            previousSearchQuery = searchQuery
            searchJob?.cancel()
            searchProducts(searchQuery)
        }
    }
}
