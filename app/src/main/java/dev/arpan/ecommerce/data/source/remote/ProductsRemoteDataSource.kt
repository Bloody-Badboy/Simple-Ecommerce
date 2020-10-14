package dev.arpan.ecommerce.data.source.remote

import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterName
import dev.arpan.ecommerce.data.model.FilterOption
import dev.arpan.ecommerce.data.model.FilterType
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.data.model.ProductDetails
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.data.model.SelectedFilterOptions
import dev.arpan.ecommerce.data.model.SortBy
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

interface ProductsRemoteDataSource {

    suspend fun getCategories(): List<ProductCategory>

    suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): List<ProductItem>

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
                val mrp = Random.nextInt(100, 1000)
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

    override suspend fun getProducts(
        category: String,
        selectedFilterOptions: SelectedFilterOptions
    ): List<ProductItem> {
        var products = mockProducts.filter { it.category == category }

        selectedFilterOptions.filterMap.entries.forEach { entry ->
            if (entry.key.value == "discount") {
                if (entry.value.isNotEmpty()) {
                    val discount = entry.value[0].value.toIntOrNull() ?: -1
                    if (discount >= 0) {
                        products = products.filter { it.discount.toInt() >= discount }
                    }
                }
            }
            if (entry.key.value == "price") {
                if (entry.value.isNotEmpty()) {
                    val splits = entry.value[0].value.split("-")
                    products = if (splits.size >= 2) {
                        val lower = splits[0].toInt()
                        val higher = splits[1].toInt()
                        products.filter { productItem -> productItem.price.toInt() in lower..higher }
                    } else {
                        val minVal = entry.value[0].value.toInt()
                        products.filter { productItem -> productItem.price.toInt() >= minVal }
                    }
                }
            }
            if (entry.key.value == "availability") {
                if (entry.value.isNotEmpty()) {
                    products = products.filter { it.inStoke }
                }
            }
        }


        return when (selectedFilterOptions.sortBy) {
            SortBy.PRICE_HIGH_TO_LOW -> {
                products.sortedByDescending { it.price }
            }
            SortBy.PRICE_LOW_HIGH -> {
                products.sortedBy { it.price }
            }
            else -> {
                products
            }
        }
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
            filterName = FilterName(
                name = "Size",
                value = "size"
            ),
            filterType = FilterType.MultipleChoice(
                options = mutableListOf<FilterOption>().apply {
                    add(FilterOption(name = "S", value = "s"))
                    add(FilterOption(name = "M", value = "m"))
                    add(FilterOption(name = "L", value = "l"))
                    add(FilterOption(name = "XL", value = "xl"))
                    add(FilterOption(name = "2XL", value = "xxl"))
                }
            )
        )

        val filterRating = Filter(
            filterName = FilterName(
                name = "Rating",
                value = "rating"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "4.5 or higher", value = "4.5"),
                    FilterOption(name = "3.5 or higher", value = "3.5"),
                    FilterOption(name = "2.5 or higher", value = "2.5"),
                    FilterOption(name = "1.5 or below", value = "1.5"),
                )
            )
        )

        val filterOptionDiscount = Filter(
            filterName = FilterName(
                name = "Discount",
                value = "discount"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "40% or more", value = "40"),
                    FilterOption(name = "30% or more", value = "30"),
                    FilterOption(name = "20% or more", value = "20"),
                    FilterOption(name = "10% or more", value = "10"),
                    FilterOption(name = "10% or below", value = "0")
                )
            )
        )

        val filterOptionPrice = Filter(
            filterName = FilterName(
                name = "Price",
                value = "price"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "₹100 or below", value = "0-100"),
                    FilterOption(name = "₹101 - ₹500", value = "101-200"),
                    FilterOption(name = "₹501 - ₹1000", value = "101-200"),
                    FilterOption(name = "₹1000 or above", value = "1000")
                )
            )
        )

        val filterOptionAvailability = Filter(
            filterName = FilterName(
                name = "Availability ",
                value = "availability"
            ),
            filterType = FilterType.SingleChoice(
                options = listOf(
                    FilterOption(name = "Exclude Out Of Stock", value = "oos")
                )
            )
        )

        if (category == "jewellery") {
            return listOf(
                filterRating,
                filterOptionDiscount,
                filterOptionPrice,
                filterOptionAvailability
            )
        } else {
            return listOf(
                filterOptionSize,
                filterRating,
                filterOptionDiscount,
                filterOptionPrice,
                filterOptionAvailability
            )
        }
    }
}
