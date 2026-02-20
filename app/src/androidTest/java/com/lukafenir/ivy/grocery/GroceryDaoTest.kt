package com.lukafenir.ivy.grocery

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroceryDaoTest {

    private lateinit var database: GroceryDatabase
    private lateinit var dao: GroceryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GroceryDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.groceryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertItem_appearsInGetAllItems() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))

        val items = dao.getAllItems().first()
        assertEquals("Should be one item", 1, items.size)
        assertEquals("The item's name should be Milk","Milk", items[0].name)
        assertEquals("The item should be unchecked", false, items[0].isChecked)
    }

    @Test
    fun insertItemWithSameName_bothAppearInGetAllItems() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))
        dao.insertItem(GroceryItem(name = "Milk"))

        val items = dao.getAllItems().first()
        assertEquals("Should be two items", 2, items.size)
        assertEquals("The first item's name should be Milk","Milk", items[0].name)
        assertEquals("The second item's name should be Milk","Milk", items[1].name)
    }

    @Test
    fun updateItem_changesItemName() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))

        val items = dao.getAllItems().first()
        assertEquals("Item should now be named cheese", "Milk", items[0].name)
        dao.updateItem(items[0].copy(name = "Cheese"))

        val updatedItems = dao.getAllItems().first()
        assertEquals("Item should now be named cheese", "Cheese", updatedItems[0].name)
    }

    @Test
    fun deleteItem_removesItemFromGetAllItems() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))
        dao.insertItem(GroceryItem(name = "Cheese"))

        val items = dao.getAllItems().first()
        assertEquals("There should be two items to begin with", 2, items.size)
        dao.deleteItem(items[0])

        val deletedItems = dao.getAllItems().first()
        assertEquals("There should be one item", 1, deletedItems.size)
        assertEquals("The item should be named Cheese", "Cheese", deletedItems[0].name)
    }

    @Test
    fun setChecked_changesItemToChecked() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))

        val items = dao.getAllItems().first()
        dao.setChecked(items[0].id, true)

        val updatedItems = dao.getAllItems().first()
        assertEquals("Item should be checked", true, updatedItems[0].isChecked)
    }

    @Test
    fun setCheckedAndUnchecked_changesItemToUnChecked() = runTest {
        dao.insertItem(GroceryItem(name = "Milk"))

        val items = dao.getAllItems().first()
        dao.setChecked(items[0].id, true)

        val updatedItems = dao.getAllItems().first()
        assertEquals("Item should be checked", true, updatedItems[0].isChecked)

        dao.setChecked(items[0].id, false)
        val finalItems = dao.getAllItems().first()
        assertEquals("Item should be unchecked", false, finalItems[0].isChecked)
    }
}
