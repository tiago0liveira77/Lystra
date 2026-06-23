package com.titos.lystra.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.titos.lystra.data.model.*
import com.titos.lystra.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AddUiState(
    val searchQuery: String = "",
    val suggestions: List<Product> = emptyList(),
    val frequentProducts: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val addedMessage: String? = null,
)

class AddViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    init {
        // Load frequent products
        viewModelScope.launch {
            repository.observeFrequentProducts(10)
                .catch { /* offline fallback */ }
                .collect { products ->
                    _uiState.update { it.copy(frequentProducts = products) }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }

        if (query.isBlank()) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }

        viewModelScope.launch {
            repository.searchProducts(query)
                .catch { /* fallback */ }
                .collect { results ->
                    _uiState.update { it.copy(suggestions = results) }
                }
        }
    }

    /**
     * Quick-add a product directly to the active list with defaults.
     */
    fun quickAdd(product: Product) {
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
            _uiState.update {
                it.copy(addedMessage = "${product.name} adicionado à lista")
            }
        }
    }

    /**
     * Add a new product from search text (not in history yet).
     * Creates the product in the catalog first, then adds to list.
     */
    fun addNewProduct(name: String): String? {
        val category = CategoryMapper.guessCategory(name)
        val product = Product(
            name = name.trim(),
            category = category.displayName,
            icon = CategoryMapper.iconForCategory(category),
        )

        var itemId: String? = null
        viewModelScope.launch {
            val productId = repository.upsertProduct(product)
            val item = ShoppingItem(
                productId = productId,
                name = product.name,
                category = product.category,
                icon = product.icon,
                quantity = 1,
                unit = "un",
                isInCart = false,
                addedAt = Timestamp.now()
            )
            itemId = repository.addToList(item)
        }
        return itemId
    }

    fun clearAddedMessage() {
        _uiState.update { it.copy(addedMessage = null) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", suggestions = emptyList(), isSearching = false) }
    }
}
