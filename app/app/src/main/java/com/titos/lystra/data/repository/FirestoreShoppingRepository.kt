package com.titos.lystra.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ShoppingItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore implementation of [ShoppingRepository].
 *
 * Uses a hardcoded userId for now (until auth is implemented).
 * Firestore SDK provides offline persistence by default on Android.
 */
class FirestoreShoppingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val userId: String = "default_user"
) : ShoppingRepository {

    // Collection references
    private val activeListRef get() = firestore
        .collection("users").document(userId)
        .collection("activeList")

    private val productsRef get() = firestore
        .collection("users").document(userId)
        .collection("products")

    // ========================================================================
    // Active Shopping List
    // ========================================================================

    override fun observeActiveList(): Flow<List<ShoppingItem>> = callbackFlow {
        val registration: ListenerRegistration = activeListRef
            .orderBy("addedAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { ShoppingItem.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun addToList(item: ShoppingItem): String {
        val docRef = activeListRef.document() // Generate ID synchronously
        docRef.set(item.toMap()) // Fire and forget for offline support
        return docRef.id
    }

    override suspend fun toggleItemCart(itemId: String, isInCart: Boolean) {
        val updates = mutableMapOf<String, Any?>(
            "isInCart" to isInCart
        )
        if (isInCart) {
            updates["checkedAt"] = Timestamp.now()
        } else {
            updates["checkedAt"] = null
        }
        activeListRef.document(itemId).update(updates)
    }

    override suspend fun updateItem(item: ShoppingItem) {
        activeListRef.document(item.id).set(item.toMap())
    }

    override suspend fun removeFromList(itemId: String) {
        activeListRef.document(itemId).delete()
    }

    override suspend fun clearCart() {
        val snapshot = activeListRef
            .whereEqualTo("isInCart", true)
            .get()
            .await()

        val batch = firestore.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)

            // Also increment purchase count in the catalog
            val productId = doc.getString("productId")
            if (!productId.isNullOrEmpty()) {
                val productRef = productsRef.document(productId)
                // We'll handle this in a separate call since batch
                // doesn't support FieldValue in the same transaction easily
            }
        }
        batch.commit().await()

        // Record purchases for catalog items
        for (doc in snapshot.documents) {
            val productId = doc.getString("productId")
            if (!productId.isNullOrEmpty()) {
                recordPurchase(productId)
            }
        }
    }

    override suspend fun getShoppingItem(itemId: String): ShoppingItem? {
        val doc = activeListRef.document(itemId).get().await()
        return doc.data?.let { ShoppingItem.fromMap(doc.id, it) }
    }

    // ========================================================================
    // Product Catalog
    // ========================================================================

    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val registration = productsRef
            .orderBy("lastPurchasedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Product.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    override fun observeFrequentProducts(limit: Int): Flow<List<Product>> = callbackFlow {
        val registration = productsRef
            .orderBy("purchaseCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Product.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    override fun searchProducts(query: String): Flow<List<Product>> = callbackFlow {
        if (query.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        // Firestore doesn't support full-text search natively.
        // We use a prefix range query on the 'name' field.
        val normalizedQuery = query.trim().lowercase()
        val endQuery = normalizedQuery + '\uf8ff'

        val registration = productsRef
            .orderBy("name")
            .startAt(normalizedQuery)
            .endAt(endQuery)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Fallback: do client-side filtering from all products
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Product.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertProduct(product: Product): String {
        return if (product.id.isNotEmpty()) {
            productsRef.document(product.id).set(product.toMap())
            product.id
        } else {
            val docRef = productsRef.document()
            docRef.set(product.toMap())
            docRef.id
        }
    }

    override suspend fun recordPurchase(productId: String) {
        val docRef = productsRef.document(productId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("purchaseCount") ?: 0
            transaction.update(docRef, mapOf(
                "purchaseCount" to currentCount + 1,
                "lastPurchasedAt" to Timestamp.now()
            ))
        }.await()
    }

    override suspend fun getProduct(productId: String): Product? {
        val doc = productsRef.document(productId).get().await()
        return doc.data?.let { Product.fromMap(doc.id, it) }
    }

    override suspend fun deleteProduct(productId: String) {
        productsRef.document(productId).delete()
    }
}
