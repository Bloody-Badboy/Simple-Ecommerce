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

package dev.arpan.ecommerce

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.result.ResultWrapper
import dev.arpan.ecommerce.ui.cart.CartViewModel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CartViewModelTest {
    companion object {
        private val CART_ITEMS: List<CartItem> = listOf(
            CartItem(
                productId = 1,
                productName = "Product 1",
                price = "50",
                "100",
                discount = "50",
                imageUrl = "",
                selectedSize = null,
                inStoke = true
            ),
            CartItem(
                productId = 2,
                productName = "Product 2",
                price = "50",
                "100",
                discount = "50",
                imageUrl = "",
                selectedSize = null,
                inStoke = true
            )
        )
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var productsRepository: ProductsRepository

    private lateinit var cartViewModel: CartViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        cartViewModel = CartViewModel(productsRepository)
    }

    @Test
    fun `priceDetails calculated correctly after fetch cart items`() =
        mainCoroutineRule.runBlockingTest {
            `when`(productsRepository.getCartProducts()).thenReturn(ResultWrapper.Success(CART_ITEMS))
            cartViewModel.fetchCartProducts()
            val priceDetails = cartViewModel.priceDetails.getOrAwaitValue()
            assertEquals(priceDetails.productCount, 2)
            assertEquals(priceDetails.totalDiscount, 100)
            assertEquals(priceDetails.totalMrp, 200)
            assertEquals(priceDetails.totalPrice, 100)
        }

    @Test
    fun `products list updated after product removed`() = mainCoroutineRule.runBlockingTest {
        `when`(productsRepository.getCartProducts()).thenReturn(ResultWrapper.Success(CART_ITEMS))
        cartViewModel.fetchCartProducts()
        val products = cartViewModel.products.getOrAwaitValue()
        cartViewModel.removeProductFromCart(1)
        val products2 = cartViewModel.products.getOrAwaitValue()
        assertNotSame(products, products2)
    }

    @Test
    fun `priceDetails updated after product removed`() = mainCoroutineRule.runBlockingTest {
        `when`(productsRepository.getCartProducts()).thenReturn(ResultWrapper.Success(CART_ITEMS))
        cartViewModel.fetchCartProducts()
        cartViewModel.removeProductFromCart(1)
        val priceDetails = cartViewModel.priceDetails.getOrAwaitValue()
        assertEquals(priceDetails.productCount, 1)
        assertEquals(priceDetails.totalDiscount, 50)
        assertEquals(priceDetails.totalMrp, 100)
        assertEquals(priceDetails.totalPrice, 50)
    }

    @Test
    fun `isCartEmpty = true if repository returns empty list`() =
        mainCoroutineRule.runBlockingTest {
            `when`(productsRepository.getCartProducts()).thenReturn(ResultWrapper.Success(emptyList()))
            cartViewModel.fetchCartProducts()
            assertTrue(cartViewModel.isCartEmpty.getOrAwaitValue())
        }

    @Test
    fun `isCartEmpty = false if repository returns empty list`() =
        mainCoroutineRule.runBlockingTest {
            `when`(productsRepository.getCartProducts()).thenReturn(ResultWrapper.Success(CART_ITEMS))
            cartViewModel.fetchCartProducts()
            assertFalse(cartViewModel.isCartEmpty.getOrAwaitValue())
        }
}
