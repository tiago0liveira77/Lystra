package com.titos.lystra.ui.navigation

/**
 * Sealed class defining all navigation destinations.
 */
sealed class Screen(val route: String) {
    /** Main shopping list with category-grouped items */
    data object List : Screen("list")

    /** Quick add screen with search + frequent products */
    data object Add : Screen("add")

    /** Product history/catalog with filters */
    data object History : Screen("history")

    /** Profile / Settings */
    data object Profile : Screen("profile")

    /** Edit/detail screen for a specific item */
    data class Edit(val itemId: String) : Screen("edit/$itemId") {
        companion object {
            const val ROUTE_TEMPLATE = "edit/{itemId}"
            const val ARG_ITEM_ID = "itemId"
        }
    }

    /** Edit screen for the global product catalog */
    data class ProductEdit(val productId: String) : Screen("productEdit/$productId") {
        companion object {
            const val ROUTE_TEMPLATE = "productEdit/{productId}"
            const val ARG_PRODUCT_ID = "productId"
        }
    }
}
