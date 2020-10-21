package dev.arpan.ecommerce.ui.product.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.arpan.ecommerce.MainCoroutineRule
import dev.arpan.ecommerce.any
import dev.arpan.ecommerce.data.DefaultProductsRepository
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.source.local.DefaultProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import dev.arpan.ecommerce.getOrAwaitValue
import dev.arpan.ecommerce.mock
import dev.arpan.ecommerce.mock.FakeProductsRemoteDataSource
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
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