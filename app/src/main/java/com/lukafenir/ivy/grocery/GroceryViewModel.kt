package com.lukafenir.ivy.grocery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroceryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GroceryRepository

    val allItems: StateFlow<List<GroceryItem>>

    init {
        val dao = GroceryDatabase.getDatabase(application).groceryDao()
        repository = GroceryRepository(dao)
        allItems = repository.allItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            repository.insert(GroceryItem(name = name))
        }
    }

    fun setChecked(id: Int, isChecked: Boolean){
        viewModelScope.launch {
            repository.setChecked(id, isChecked)
        }
    }

    fun deleteItem(item: GroceryItem){
        viewModelScope.launch {
            repository.delete(item)
        }
    }

}