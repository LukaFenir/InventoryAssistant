package com.lukafenir.ivy.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroceryViewModel(private val repository: GroceryRepository) : ViewModel() {

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds.asStateFlow()

    val isInSelectionMode: StateFlow<Boolean> = _selectedIds
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allItems: StateFlow<List<GroceryItem>> = repository.allItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    fun deleteSelected(){
        viewModelScope.launch {
            val items = allItems.value.filter { it.id in _selectedIds.value }
            items.forEach { repository.delete(it) }
            _selectedIds.value = emptySet()
        }
    }

    fun toggleSelection(id: Int) {
        _selectedIds.value = if (id in _selectedIds.value) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

}