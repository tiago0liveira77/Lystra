package com.titos.lystra.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titos.lystra.data.model.ShoppingItem
import com.titos.lystra.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the shopping list screen.
 */
data class ListUiState(
    /** Items grouped by category, sorted: pending first, then in-cart */
    val pendingByCategory: Map<String, List<ShoppingItem>> = emptyMap(),
    val cartItems: List<ShoppingItem> = emptyList(),
    val cartCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
)

/**
 * ViewModel for the shopping list screen.
 */
class ListViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeActiveList()
                .catch { /* handle error silently for offline mode */ }
                .collect { items ->
                    val pending = items.filter { !it.isInCart }
                    val cart = items.filter { it.isInCart }

                    // Group pending items by category
                    val grouped = pending.groupBy { it.category }
                        .toSortedMap() // Alphabetical category order

                    _uiState.value = ListUiState(
                        pendingByCategory = grouped,
                        cartItems = cart,
                        cartCount = cart.size,
                        totalCount = items.size,
                        isLoading = false,
                        isEmpty = items.isEmpty()
                    )
                }
        }
    }

    /**
     * Toggle an item between "Falta Comprar" and "No Carrinho".
     */
    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            try {
                repository.toggleItemCart(item.id, !item.isInCart)
            } catch (_: Exception) {
                // Firestore will sync when back online
            }
        }
    }

    /**
     * Remove an item from the list entirely.
     */
    fun removeItem(item: ShoppingItem) {
        viewModelScope.launch {
            try {
                repository.removeFromList(item.id)
            } catch (_: Exception) {
                // Firestore will sync when back online
            }
        }
    }

    /**
     * Clear all items in the cart (end of shopping session).
     */
    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart()
            } catch (_: Exception) {
                // Will sync later
            }
        }
    }
}
