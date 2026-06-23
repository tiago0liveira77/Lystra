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

    private var allProducts: List<Product> = emptyList()

    init {
        // Load frequent products
        viewModelScope.launch {
            repository.observeFrequentProducts(10)
                .catch { /* offline fallback */ }
                .collect { products ->
                    _uiState.update { it.copy(frequentProducts = products) }
                }
        }

        // Cache all products for fast client-side case-insensitive search
        viewModelScope.launch {
            repository.observeProducts()
                .catch { /* fallback */ }
                .collect { products ->
                    allProducts = products
                    // Update suggestions if currently searching
                    if (_uiState.value.isSearching) {
                        filterProductsLocally(_uiState.value.searchQuery)
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }

        if (query.isBlank()) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }

        filterProductsLocally(query)
    }

    private fun filterProductsLocally(query: String) {
        val normalizedQuery = query.trim().lowercase()
        val results = allProducts.filter {
            it.name.lowercase().contains(normalizedQuery)
        }.take(10) // Limit suggestions to 10
        
        _uiState.update { it.copy(suggestions = results) }
    }

    /**
     * Quick-add a product directly to the active list with defaults.
     */
    fun quickAdd(product: Product, onAdded: ((String) -> Unit)? = null) {
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
            val id = repository.addToList(item)
            _uiState.update {
                it.copy(addedMessage = "${product.name} adicionado à lista")
            }
            onAdded?.invoke(id)
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
