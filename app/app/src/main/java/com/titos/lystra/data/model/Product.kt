package com.titos.lystra.data.model

import com.google.firebase.Timestamp

/**
 * Represents a product in the master catalog.
 * Products are never deleted — they persist as the user's purchase history.
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "Outros",
    val icon: String = "shopping_cart",
    val defaultUnit: String = "un",
    val defaultQuantity: Int = 1,
    val purchaseCount: Int = 0,
    val lastPurchasedAt: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val notes: String = ""
) {
    /** Firestore-friendly map for serialization */
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "category" to category,
        "icon" to icon,
        "defaultUnit" to defaultUnit,
        "defaultQuantity" to defaultQuantity,
        "purchaseCount" to purchaseCount,
        "lastPurchasedAt" to lastPurchasedAt,
        "createdAt" to createdAt,
        "notes" to notes
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Product = Product(
            id = id,
            name = map["name"] as? String ?: "",
            category = map["category"] as? String ?: "Outros",
            icon = map["icon"] as? String ?: "shopping_cart",
            defaultUnit = map["defaultUnit"] as? String ?: "un",
            defaultQuantity = (map["defaultQuantity"] as? Long)?.toInt() ?: 1,
            purchaseCount = (map["purchaseCount"] as? Long)?.toInt() ?: 0,
            lastPurchasedAt = map["lastPurchasedAt"] as? Timestamp,
            createdAt = map["createdAt"] as? Timestamp ?: Timestamp.now(),
            notes = map["notes"] as? String ?: ""
        )
    }
}
