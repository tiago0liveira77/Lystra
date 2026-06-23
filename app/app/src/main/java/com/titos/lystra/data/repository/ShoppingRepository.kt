package com.titos.lystra.data.repository

import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for all shopping list data operations.
 * Abstraction over the Firestore implementation.
 */
interface ShoppingRepository {

    // ========================================================================
    // Active Shopping List
    // ========================================================================

    /** Observe the active shopping list in real-time */
    fun observeActiveList(): Flow<List<ShoppingItem>>

    /** Add an item to the active list */
    suspend fun addToList(item: ShoppingItem): String

    /** Toggle an item between "Falta Comprar" and "No Carrinho" */
    suspend fun toggleItemCart(itemId: String, isInCart: Boolean)

    /** Update an existing list item (quantity, unit, notes) */
    suspend fun updateItem(item: ShoppingItem)

    /** Remove a single item from the active list */
    suspend fun removeFromList(itemId: String)

    /** Clear all "No Carrinho" items from the active list (end of shopping) */
    suspend fun clearCart()

    /** Get a single shopping item by ID */
    suspend fun getShoppingItem(itemId: String): ShoppingItem?

    // ========================================================================
    // Product Catalog (History)
    // ========================================================================

    /** Observe all products in the catalog */
    fun observeProducts(): Flow<List<Product>>

    /** Get products sorted by purchase frequency */
    fun observeFrequentProducts(limit: Int = 10): Flow<List<Product>>

    /** Search products by name (prefix match) */
    fun searchProducts(query: String): Flow<List<Product>>

    /** Add or update a product in the catalog */
    suspend fun upsertProduct(product: Product): String

    /** Increment the purchase count for a product */
    suspend fun recordPurchase(productId: String)
}
