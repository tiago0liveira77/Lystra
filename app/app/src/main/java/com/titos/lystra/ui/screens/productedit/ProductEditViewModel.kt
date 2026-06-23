package com.titos.lystra.ui.screens.productedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titos.lystra.data.model.CategoryMapper
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ProductCategory
import com.titos.lystra.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductEditUiState(
    val product: Product? = null,
    val name: String = "",
    val category: ProductCategory = ProductCategory.OUTROS,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
)

class ProductEditViewModel(
    private val repository: ShoppingRepository,
    private val productId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductEditUiState())
    val uiState: StateFlow<ProductEditUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            if (product != null) {
                _uiState.update {
                    it.copy(
                        product = product,
                        name = product.name,
                        category = ProductCategory.fromDisplayName(product.category),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateCategory(category: ProductCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun save() {
        viewModelScope.launch {
            val currentProduct = _uiState.value.product ?: return@launch
            val updated = currentProduct.copy(
                name = _uiState.value.name.trim(),
                category = _uiState.value.category.displayName,
                icon = CategoryMapper.iconForCategory(_uiState.value.category)
            )
            repository.upsertProduct(updated)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
