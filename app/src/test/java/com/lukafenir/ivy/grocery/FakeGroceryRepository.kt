package com.lukafenir.ivy.grocery

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGroceryRepository : GroceryRepository {

    private val items = mutableListOf<GroceryItem>()
    private val itemsFlow = MutableStateFlow<List<GroceryItem>>(emptyList())
    private var nextId = 1

    override val allItems: Flow<List<GroceryItem>> = itemsFlow

    override suspend fun insert(item: GroceryItem) {
        val newItem = item.copy(id = nextId++)
        items.add(newItem)
        itemsFlow.value = items.toList()
    }

    override suspend fun update(item: GroceryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            itemsFlow.value = items.toList()
        }
    }

    override suspend fun delete(item: GroceryItem) {
        items.removeAll { it.id == item.id }
        itemsFlow.value = items.toList()
    }

    override suspend fun setChecked(id: Int, isChecked: Boolean) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items[index] = items[index].copy(isChecked = isChecked)
            itemsFlow.value = items.toList()
        }
    }
}
