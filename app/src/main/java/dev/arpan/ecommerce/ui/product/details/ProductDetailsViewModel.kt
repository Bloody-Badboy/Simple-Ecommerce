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

package dev.arpan.ecommerce.ui.product.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AddToCart
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.result.Event
import dev.arpan.ecommerce.result.ResultWrapper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) :
    ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _productDetail = MutableLiveData<ProductDetails>()
    val productDetails: LiveData<ProductDetails>
        get() = _productDetail

    private val _addToCartState = MutableLiveData<Event<AddToCartState>>()
    val addToCartState: LiveData<Event<AddToCartState>>
        get() = _addToCartState

    private val _isProductInCart = MutableLiveData<Boolean>()
    val isProductInCart: LiveData<Boolean>
        get() = _isProductInCart

    val cartItemCount = productsRepository.cartItemCountFlow.asLiveData()

    fun fetchProductDetails(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            val response = productsRepository.getProductDetails(productId)
            _isProductInCart.value = productsRepository.isProductInCart(productId)

            _isLoading.value = false

            when (response) {
                is ResultWrapper.NetworkError -> {
                }
                is ResultWrapper.GenericError -> {
                }
                is ResultWrapper.Success -> {
                    _productDetail.value = response.value
                }
            }
        }
    }

    fun addItemToCart(productId: Long, selectedSize: String?) {
        viewModelScope.launch {
            val addToCart = AddToCart(
                productId = productId,
                selectedSize = selectedSize
            )

            _addToCartState.value = Event(AddToCartState.Adding)
            if (productsRepository.addProductToCart(addToCart)) {
                _addToCartState.value = Event(AddToCartState.Added)
                _isProductInCart.value = true
            }
        }
    }
}

sealed class AddToCartState {
    object Adding : AddToCartState()
    object Added : AddToCartState()
}
