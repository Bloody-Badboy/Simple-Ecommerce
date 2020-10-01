package dev.arpan.ecommerce.data.source.remote

import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

interface ProductsRemoteDataSource {

    suspend fun getCategories(): List<ProductCategory>

    suspend fun getProducts(category: String): List<ProductItem>

    suspend fun getProductDetails(productId: Long): ProductDetails
}

class DefaultProductsRemoteDataSource : ProductsRemoteDataSource {

    private val mockProducts = mutableListOf<ProductItem>()
    private val mockCategories = listOf(
        ProductCategory(name = "Top Wear", "top_wear"),
        ProductCategory(name = "Bottom Wear", "bottom_wear"),
        ProductCategory(name = "Jewellery", "jewellery")
    )

    init {
        mockProducts.apply {
            for (i in 0 until 100) {
                val categoryIndex = Random.nextInt(mockCategories.size)
                val inStock = Random.nextBoolean()
                val mrp = Random.nextInt(1000)
                val discount = max(10, Random.nextInt(50))

                add(
                    ProductItem(
                        productId = i.toLong(),
                        productName = "Product ${mockCategories[categoryIndex].name} #${i + 1}",
                        price = (mrp - mrp * (discount / 100f)).roundToInt().toString(),
                        mrp = mrp.toString(),
                        discount = discount.toString(),
                        imageUrl = "https://picsum.photos/seed/$i/200/300?random=${i + 1}",
                        category = mockCategories[categoryIndex].endPoint,
                        tag = if (Random.nextBoolean() && inStock) "New Arrival" else null,
                        isWishlisted = Random.nextBoolean(),
                        inStoke = inStock
                    )
                )
            }
        }
    }

    override suspend fun getCategories(): List<ProductCategory> {
        return mockCategories
    }

    override suspend fun getProducts(category: String): List<ProductItem> {
        return mockProducts.filter { it.category == category }
    }

    override suspend fun getProductDetails(productId: Long): ProductDetails {
        val product = mockProducts.first { it.productId == productId }
        return ProductDetails(
            productId = product.productId,
            productName = product.productName,
            price = product.price,
            mrp = product.mrp,
            discount = product.discount,
            imageUrls = listOf(
                "https://picsum.photos/seed/${productId}/480/640?random=${UUID.randomUUID()}",
                "https://picsum.photos/seed/${productId + 1}/480/640?random=${UUID.randomUUID()}",
                "https://picsum.photos/seed/${productId + 2} /480/640?random=${UUID.randomUUID()}",
                "https://picsum.photos/seed/${productId + 3}/480/640?random=${UUID.randomUUID()}"
            ),
            isWishlisted = product.isWishlisted,
            inStoke = product.inStoke,
            availableSize = if (product.category == "jewellery" ) emptyList() else listOf("S", "2XL"),
            suggestedProducts = mockProducts.filter { it.category == product.category && it.productId != productId }
                .take(8)
        )
    }
}
