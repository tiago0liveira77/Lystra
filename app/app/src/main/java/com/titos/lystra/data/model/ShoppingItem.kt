package com.titos.lystra.data.model

import com.google.firebase.Timestamp

/**
 * Represents an item in the active shopping list.
 * Denormalizes product data for fast offline reads.
 *
 * State machine: "Falta Comprar" (isInCart=false) ↔ "No Carrinho" (isInCart=true)
 */
data class ShoppingItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val category: String = "Outros",
    val icon: String = "shopping_cart",
    val quantity: Int = 1,
    val unit: String = "un",
    val notes: String = "",
    val isInCart: Boolean = false,
    val checkedAt: Timestamp? = null,
    val addedAt: Timestamp = Timestamp.now(),
    val checkedBy: String? = null
) {
    /** Firestore-friendly map for serialization */
    fun toMap(): Map<String, Any?> = mapOf(
        "productId" to productId,
        "name" to name,
        "category" to category,
        "icon" to icon,
        "quantity" to quantity,
        "unit" to unit,
        "notes" to notes,
        "isInCart" to isInCart,
        "checkedAt" to checkedAt,
        "addedAt" to addedAt,
        "checkedBy" to checkedBy
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): ShoppingItem = ShoppingItem(
            id = id,
            productId = map["productId"] as? String ?: "",
            name = map["name"] as? String ?: "",
            category = map["category"] as? String ?: "Outros",
            icon = map["icon"] as? String ?: "shopping_cart",
            quantity = (map["quantity"] as? Long)?.toInt() ?: 1,
            unit = map["unit"] as? String ?: "un",
            notes = map["notes"] as? String ?: "",
            isInCart = map["isInCart"] as? Boolean ?: false,
            checkedAt = map["checkedAt"] as? Timestamp,
            addedAt = map["addedAt"] as? Timestamp ?: Timestamp.now(),
            checkedBy = map["checkedBy"] as? String
        )
    }
}
