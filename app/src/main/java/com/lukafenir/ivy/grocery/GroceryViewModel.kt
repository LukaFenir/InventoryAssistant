package com.lukafenir.ivy.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroceryViewModel(private val repository: GroceryRepository) : ViewModel() {

    private val _isInManagementMode = MutableStateFlow<Boolean>(false)

    val isInManagementMode: StateFlow<Boolean> = _isInManagementMode.asStateFlow()

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

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun enterManagementMode() {
        _isInManagementMode.value = true
    }

    fun exitManagementMode() {
        _isInManagementMode.value = false
    }

}
