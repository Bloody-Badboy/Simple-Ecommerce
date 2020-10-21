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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.arpan.ecommerce.MainCoroutineRule
import dev.arpan.ecommerce.data.DefaultProductsRepository
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.source.local.DefaultProductsLocalDataSource
import dev.arpan.ecommerce.getOrAwaitValue
import dev.arpan.ecommerce.mock.FakeProductsRemoteDataSource
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ProductDetailsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var productsRepository: ProductsRepository

    private lateinit var productDetailsViewModel: ProductDetailsViewModel

    @Before
    fun setup() = mainCoroutineRule.runBlockingTest {
        MockitoAnnotations.initMocks(this)
        productsRepository = DefaultProductsRepository(
            DefaultProductsLocalDataSource(),
            FakeProductsRemoteDataSource(),
            mainCoroutineRule.dispatcher
        )
        productDetailsViewModel = ProductDetailsViewModel(productsRepository)
    }

    @Test
    fun `product details loaded correctly`() = mainCoroutineRule.runBlockingTest {
        productDetailsViewModel.fetchProductDetails(1)
        assertEquals(productDetailsViewModel.productDetails.getOrAwaitValue().productId, 1)
    }
}
