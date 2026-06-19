package com.lukafenir.ivy.grocery

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class FakeGroceryRepository : GroceryRepository {

    private val items = mutableListOf<GroceryItem>()
    private val itemsFlow = MutableStateFlow<List<GroceryItem>>(emptyList())
    private var nextId = 1
    var shouldHangOnDelete = false

    var deleteCallCount = 0

    override val allItems: Flow<List<GroceryItem>> = itemsFlow

    override suspend fun insert(item: GroceryItem): Long {
        val assignedId = nextId++
        items.add(item.copy(id = assignedId))
        itemsFlow.value = items.toList()
        return assignedId.toLong()
    }

    override suspend fun update(item: GroceryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            itemsFlow.value = items.toList()
        }
    }

    override suspend fun delete(item: GroceryItem) {
        deleteCallCount++
        if (shouldHangOnDelete) suspendCancellableCoroutine<Unit> { /* never resumes */ }
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
