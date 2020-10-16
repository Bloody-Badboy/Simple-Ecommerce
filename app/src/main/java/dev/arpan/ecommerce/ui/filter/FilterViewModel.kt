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
