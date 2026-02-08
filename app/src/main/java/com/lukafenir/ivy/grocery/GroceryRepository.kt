package com.lukafenir.ivy.grocery

import kotlinx.coroutines.flow.Flow

class GroceryRepository(private val groceryDao: GroceryDao) {

    val allItems: Flow<List<GroceryItem>> = groceryDao.getAllItems()

    suspend fun insert(item: GroceryItem){
        groceryDao.insertItem(item)
    }

    suspend fun update(item: GroceryItem){
        groceryDao.updateItem(item)
    }

    suspend fun delete(item: GroceryItem){
        groceryDao.deleteItem(item)
    }

    suspend fun setChecked(id: Int, isChecked: Boolean){
        groceryDao.setChecked(id, isChecked)
    }
}