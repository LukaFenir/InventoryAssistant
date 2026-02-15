package com.lukafenir.ivy.grocery

import kotlinx.coroutines.flow.Flow

class RoomGroceryRepository(private val groceryDao: GroceryDao) : GroceryRepository {

    override val allItems: Flow<List<GroceryItem>> = groceryDao.getAllItems()

    override suspend fun insert(item: GroceryItem){
        groceryDao.insertItem(item)
    }

    override suspend fun update(item: GroceryItem){
        groceryDao.updateItem(item)
    }

    override suspend fun delete(item: GroceryItem){
        groceryDao.deleteItem(item)
    }

    override suspend fun setChecked(id: Int, isChecked: Boolean){
        groceryDao.setChecked(id, isChecked)
    }
}