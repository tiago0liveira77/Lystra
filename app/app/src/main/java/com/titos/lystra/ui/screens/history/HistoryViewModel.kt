package com.titos.lystra.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ProductCategory
import com.titos.lystra.data.model.ShoppingItem
import com.titos.lystra.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class HistoryUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "All Time",
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
)

class HistoryViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProducts()
                .catch { /* offline */ }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            products = products,
                            filteredProducts = applyFilters(products, it.searchQuery, it.selectedFilter),
                            totalCount = products.size,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredProducts = applyFilters(state.products, query, state.selectedFilter)
            )
        }
    }

    fun selectFilter(filter: String) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredProducts = applyFilters(state.products, state.searchQuery, filter)
            )
        }
    }

    fun addToList(product: Product) {
        viewModelScope.launch {
            val item = ShoppingItem(
                productId = product.id,
                name = product.name,
                category = product.category,
                icon = product.icon,
                quantity = product.defaultQuantity,
                unit = product.defaultUnit,
                notes = product.notes,
                isInCart = false,
                addedAt = Timestamp.now()
            )
            repository.addToList(item)
        }
    }

    private fun applyFilters(products: List<Product>, query: String, filter: String): List<Product> {
        var result = products

        // Search filter
        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter { it.name.lowercase().contains(q) }
        }

        // Category filter
        when (filter) {
            "All Time" -> { /* no filter */ }
            "Freq Bought" -> result = result.sortedByDescending { it.purchaseCount }
            else -> {
                // Category name filter
                result = result.filter { it.category.equals(filter, ignoreCase = true) }
            }
        }

        return result
    }

    companion object {
        /**
         * Calculate a human-readable time ago string from a Timestamp.
         */
        fun timeAgoString(timestamp: Timestamp?): String {
            if (timestamp == null) return "Sem data"

            val now = System.currentTimeMillis()
            val then = timestamp.toDate().time
            val diff = now - then

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val weeks = days / 7
            val months = days / 30

            return when {
                minutes < 1 -> "Agora mesmo"
                minutes < 60 -> "Há ${minutes}min"
                hours < 24 -> "Há ${hours}h"
                days < 2 -> "Ontem"
                days < 7 -> "Há $days dias"
                weeks < 5 -> if (weeks == 1L) "Há 1 semana" else "Há $weeks semanas"
                months < 12 -> if (months == 1L) "Há 1 mês" else "Há $months meses"
                else -> "Há mais de 1 ano"
            }
        }
    }
}
