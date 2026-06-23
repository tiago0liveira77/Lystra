package com.titos.lystra.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titos.lystra.data.model.ShoppingItem
import com.titos.lystra.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditUiState(
    val item: ShoppingItem? = null,
    val quantity: Int = 1,
    val unit: String = "un",
    val notes: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
)

class EditViewModel(
    private val repository: ShoppingRepository,
    private val itemId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    init {
        loadItem()
    }

    private fun loadItem() {
        viewModelScope.launch {
            val item = repository.getShoppingItem(itemId)
            if (item != null) {
                _uiState.update {
                    it.copy(
                        item = item,
                        quantity = item.quantity,
                        unit = item.unit,
                        notes = item.notes,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateQuantity(quantity: Int) {
        _uiState.update { it.copy(quantity = quantity.coerceIn(1, 99)) }
    }

    fun updateUnit(unit: String) {
        _uiState.update { it.copy(unit = unit) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun save() {
        viewModelScope.launch {
            val currentItem = _uiState.value.item ?: return@launch
            val updated = currentItem.copy(
                quantity = _uiState.value.quantity,
                unit = _uiState.value.unit,
                notes = _uiState.value.notes
            )
            repository.updateItem(updated)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.removeFromList(itemId)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
