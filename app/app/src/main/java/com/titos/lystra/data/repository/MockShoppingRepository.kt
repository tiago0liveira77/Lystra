package com.titos.lystra.data.repository

import com.google.firebase.Timestamp
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ProductCategory
import com.titos.lystra.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * In-memory implementation of [ShoppingRepository] for development and testing.
 * Use this until a valid google-services.json is provided for Firestore.
 */
class MockShoppingRepository : ShoppingRepository {

    private val activeList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    private val products = MutableStateFlow<List<Product>>(emptyList())

    init {
        // Populate some initial mock data
        val mockProducts = listOf(
            Product(id = "1", name = "Maçã Gala", category = "Frutas e Vegetais", icon = "eco", purchaseCount = 12),
            Product(id = "2", name = "Leite Meio Gordo", category = "Laticínios", icon = "water_drop", purchaseCount = 20),
            Product(id = "3", name = "Pão de Forma", category = "Padaria", icon = "bakery_dining", purchaseCount = 15),
            Product(id = "4", name = "Peito de Frango", category = "Carnes e Peixe", icon = "set_meal", purchaseCount = 8)
        )
        products.value = mockProducts

        activeList.value = listOf(
            ShoppingItem(id = "101", productId = "1", name = "Maçã Gala", category = "Frutas e Vegetais", quantity = 6, isInCart = false),
            ShoppingItem(id = "102", productId = "2", name = "Leite Meio Gordo", category = "Laticínios", quantity = 2, unit = "l", isInCart = true, checkedAt = Timestamp.now())
        )
    }

    override fun observeActiveList(): Flow<List<ShoppingItem>> = activeList

    override suspend fun addToList(item: ShoppingItem): String {
        val newItem = item.copy(id = UUID.randomUUID().toString())
        activeList.update { it + newItem }
        return newItem.id
    }

    override suspend fun toggleItemCart(itemId: String, isInCart: Boolean) {
        activeList.update { list ->
            list.map {
                if (it.id == itemId) {
                    it.copy(
                        isInCart = isInCart,
                        checkedAt = if (isInCart) Timestamp.now() else null
                    )
                } else {
                    it
                }
            }
        }
    }

    override suspend fun updateItem(item: ShoppingItem) {
        activeList.update { list ->
            list.map { if (it.id == item.id) item else it }
        }
    }

    override suspend fun removeFromList(itemId: String) {
        activeList.update { list -> list.filter { it.id != itemId } }
    }

    override suspend fun clearCart() {
        val itemsToBuy = activeList.value.filter { it.isInCart }
        activeList.update { list -> list.filter { !it.isInCart } }
        
        // Update purchase counts for bought items
        itemsToBuy.forEach { item ->
            if (item.productId.isNotEmpty()) {
                recordPurchase(item.productId)
            }
        }
    }

    override suspend fun getShoppingItem(itemId: String): ShoppingItem? {
        return activeList.value.find { it.id == itemId }
    }

    override fun observeProducts(): Flow<List<Product>> = products

    override fun observeFrequentProducts(limit: Int): Flow<List<Product>> {
        return products.map { list ->
            list.sortedByDescending { it.purchaseCount }.take(limit)
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return products.map { list ->
            if (query.isBlank()) {
                emptyList()
            } else {
                val q = query.lowercase()
                list.filter { it.name.lowercase().contains(q) }
            }
        }
    }

    override suspend fun upsertProduct(product: Product): String {
        var id = product.id
        if (id.isEmpty()) {
            id = UUID.randomUUID().toString()
            val newProduct = product.copy(id = id)
            products.update { it + newProduct }
        } else {
            products.update { list ->
                list.map { if (it.id == id) product else it }
            }
        }
        return id
    }

    override suspend fun getProduct(productId: String): Product? {
        return products.value.find { it.id == productId }
    }

    override suspend fun deleteProduct(productId: String) {
        products.update { list -> list.filterNot { it.id == productId } }
    }

    override suspend fun recordPurchase(productId: String) {
        products.update { list ->
            list.map {
                if (it.id == productId) {
                    it.copy(
                        purchaseCount = it.purchaseCount + 1,
                        lastPurchasedAt = Timestamp.now()
                    )
                } else {
                    it
                }
            }
        }
    }
}
