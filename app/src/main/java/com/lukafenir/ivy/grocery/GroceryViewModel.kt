package com.lukafenir.ivy.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroceryViewModel(private val repository: GroceryRepository) : ViewModel() {

    val allItems: StateFlow<List<GroceryItem>> = repository.allItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds.asStateFlow()

    val isInSelectionMode: StateFlow<Boolean> = _selectedIds
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun addItem(name: String) {
        viewModelScope.launch {
            if(name.isNotBlank()) {
                repository.insert(GroceryItem(name = name.trim()))
            }
        }
    }

    fun setChecked(id: Int, isChecked: Boolean){
        viewModelScope.launch {
            repository.setChecked(id, isChecked)
        }
    }

    fun toggleSelection(id: Int) {
        _selectedIds.value = if (id in _selectedIds.value) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun deleteSelected() {
        val itemsToDelete = allItems.value.filter { it.id in _selectedIds.value }
        _selectedIds.value = emptySet()
        viewModelScope.launch {
            itemsToDelete.forEach { repository.delete(it) }
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

}
