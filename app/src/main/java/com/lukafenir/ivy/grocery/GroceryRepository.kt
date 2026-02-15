package com.lukafenir.ivy.grocery

import kotlinx.coroutines.flow.Flow

interface GroceryRepository {

    val allItems: Flow<List<GroceryItem>>

    suspend fun insert(item: GroceryItem)

    suspend fun update(item: GroceryItem)

    suspend fun delete(item: GroceryItem)

    suspend fun setChecked(id: Int, isChecked: Boolean)

}