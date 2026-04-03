package com.lukafenir.ivy.grocery

import android.util.Log
import kotlinx.coroutines.flow.Flow

class SyncedGroceryRepository (
    private val local: GroceryRepository,
    private val remote: GroceryRepository
) : GroceryRepository {

    override val allItems: Flow<List<GroceryItem>>
        get() = local.allItems

    override suspend fun insert(item: GroceryItem) : Long {
        val generatedId = local.insert(item)
        remote.insert(item.copy(id = generatedId.toInt()))
        Log.d("SyncedGroceryRepository", "Item inserted in local and remote: $item")
        return generatedId
    }

    override suspend fun update(item: GroceryItem) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: GroceryItem) {
        TODO("Not yet implemented")
    }

    override suspend fun setChecked(id: Int, isChecked: Boolean) {
        TODO("Not yet implemented")
    }
}