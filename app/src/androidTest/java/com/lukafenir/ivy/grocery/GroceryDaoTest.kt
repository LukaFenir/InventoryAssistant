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
        assertEquals(1, items.size)
        assertEquals("Milk", items[0].name)
        assertEquals(false, items[0].isChecked)
    }
}
