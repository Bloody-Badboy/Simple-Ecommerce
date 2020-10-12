package dev.arpan.ecommerce.data.source.remote

import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterNameValue
import dev.arpan.ecommerce.data.model.FilterType
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

    suspend fun searchProduct(query: String): List<ProductItem>

    suspend fun getProductDetails(productId: Long): ProductDetails

    suspend fun getFiltersForCategory(category: String): List<Filter>
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
                        category = mockCategories[categoryIndex].value,
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

    override suspend fun searchProduct(query: String): List<ProductItem> {
        return mockProducts.filter { it.productName.contains(query) }
    }

    override suspend fun getProductDetails(productId: Long): ProductDetails {
        val product = mockProducts.first { it.productId == productId }
        val allSizes = listOf(
            "S",
            "M",
            "L",
            "XL",
            "2XL"
        )
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
            rating = Random.nextDouble(1.toDouble(), 5.toDouble()),
            availableSize = if (product.category == "jewellery") emptyList() else allSizes.subList(
                0,
                Random.nextInt(1, allSizes.size)
            ),
            suggestedProducts = mockProducts.filter { it.category == product.category && it.productId != productId }
                .take(8)
        )
    }

    override suspend fun getFiltersForCategory(category: String): List<Filter> {
        val filterOptionSize = Filter(
            filterNameValue = FilterNameValue(
                name = "Size",
                value = "size"
            ),
            filterType = FilterType.MultipleChoice(
                options = mutableListOf<FilterNameValue>().apply {
                    add(FilterNameValue(name = "S", value = "s"))
                    add(FilterNameValue(name = "M", value = "m"))
                    add(FilterNameValue(name = "L", value = "l"))
                    add(FilterNameValue(name = "XL", value = "xl"))
                    add(FilterNameValue(name = "2XL", value = "xxl"))
                }
            )
        )

        val filterRating = Filter(
            filterNameValue = FilterNameValue(
                name = "Rating",
                value = "rating"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterNameValue(name = "4.5 or higher", value = "4.5"),
                    FilterNameValue(name = "3.5 or higher", value = "3.5"),
                    FilterNameValue(name = "2.5 or higher", value = "2.5"),
                    FilterNameValue(name = "1.5 or below", value = "1.5"),
                )
            )
        )

        val filterOptionDiscount = Filter(
            filterNameValue = FilterNameValue(
                name = "Discount",
                value = "discount"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterNameValue(name = "40% or more", value = "40"),
                    FilterNameValue(name = "30% or more", value = "30"),
                    FilterNameValue(name = "20% or more", value = "20"),
                    FilterNameValue(name = "10% or more", value = "10"),
                    FilterNameValue(name = "10% or below", value = "0")
                )
            )
        )

        if (category == "jewellery") {
            return listOf(filterRating, filterOptionDiscount)
        } else {
            return listOf(filterOptionSize, filterRating, filterOptionDiscount)
        }
    }
}
