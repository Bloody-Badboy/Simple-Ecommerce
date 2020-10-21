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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.arpan.ecommerce.MainCoroutineRule
import dev.arpan.ecommerce.data.DefaultProductsRepository
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.AppliedFilterMap
import dev.arpan.ecommerce.data.model.FilterName
import dev.arpan.ecommerce.data.model.FilterOption
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.data.source.local.DefaultProductsLocalDataSource
import dev.arpan.ecommerce.getOrAwaitValue
import dev.arpan.ecommerce.mock.FakeProductsRemoteDataSource
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class ProductListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    
    private lateinit var productsRepository: ProductsRepository

    private lateinit var productListViewModel: ProductListViewModel

    @Before
    fun setup() = mainCoroutineRule.runBlockingTest {
        MockitoAnnotations.initMocks(this)
        productsRepository = DefaultProductsRepository(
            DefaultProductsLocalDataSource(),
            FakeProductsRemoteDataSource(),
            mainCoroutineRule.dispatcher
        )
        productListViewModel = ProductListViewModel(productsRepository)
    }

    @Test
    fun `product list by category loaded correctly`() = mainCoroutineRule.runBlockingTest {
        productListViewModel.fetchProducts("top_wear", SortBy.POPULARITY, emptyMap())
        assertEquals(productListViewModel.products.getOrAwaitValue().size, 5)

        productListViewModel.fetchProducts("bottom_wear", SortBy.POPULARITY, emptyMap())
        assertEquals(productListViewModel.products.getOrAwaitValue().size, 5)
    }

    @Test
    fun `sort by changes only for observed category`() = mainCoroutineRule.runBlockingTest {

        productsRepository.setSelectedSortByForCategory("top_wear", SortBy.PRICE_LOW_HIGH)
        productsRepository.setSelectedSortByForCategory("bottom_wear", SortBy.PRICE_HIGH_TO_LOW)

        productListViewModel.observeChangesForCategory("top_wear")
        assertEquals(productListViewModel.sortBy.getOrAwaitValue(), SortBy.PRICE_LOW_HIGH)

        productListViewModel.observeChangesForCategory("bottom_wear")
        assertEquals(productListViewModel.sortBy.getOrAwaitValue(), SortBy.PRICE_HIGH_TO_LOW)
    }

    @Test
    fun `applied filters changes only for observed category`() = mainCoroutineRule.runBlockingTest {

        val filterMap: AppliedFilterMap = mapOf(
            FilterName(
                name = "Size",
                value = "size"
            ) to listOf(
                FilterOption(name = "S", value = "s"),
                FilterOption(name = "M", value = "m"),
                FilterOption(name = "L", value = "l"),
                FilterOption(name = "XL", value = "xl"),
                FilterOption(name = "2XL", value = "xxl")
            )
        )
        productsRepository.setAppliedFilterForCategory("top_wear", filterMap)
        productsRepository.setAppliedFilterForCategory("bottom_wear", filterMap)

        productListViewModel.observeChangesForCategory("top_wear")
        assertEquals(productListViewModel.appliedFilterMap.getOrAwaitValue(), filterMap)

        productListViewModel.observeChangesForCategory("bottom_wear")
        assertEquals(productListViewModel.appliedFilterMap.getOrAwaitValue(), filterMap)
    }
}
