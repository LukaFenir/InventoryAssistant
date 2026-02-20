package com.lukafenir.ivy.grocery

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryViewModelTest {

    private lateinit var repository: FakeGroceryRepository
    private lateinit var viewModel: GroceryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeGroceryRepository()
        viewModel = GroceryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addItem_insertsItemIntoRepository() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")

        val items = viewModel.allItems.value
        assertEquals("Should be one item", 1, items.size)
        assertEquals("The item's name should be Milk", "Milk", items[0].name)
        assertEquals("The item should be unchecked", false, items[0].isChecked)

        collectJob.cancel()
    }

    @Test
    fun setChecked_updatesItemInRepositoryToChecked() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")

        viewModel.setChecked(viewModel.allItems.value[0].id, true)

        val items = viewModel.allItems.value
        assertEquals("Should be one item", 1, items.size)
        assertEquals("The item should be checked", true, items[0].isChecked)

        collectJob.cancel()
    }

    @Test
    fun deleteItem_deletesItemFromRepository() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")
        viewModel.addItem("Cheese")
        viewModel.addItem("Banana")

        assertEquals("Should be three items", 3, viewModel.allItems.value.size)

        viewModel.deleteItem(viewModel.allItems.value[1])
        val items = viewModel.allItems.value
        assertEquals("Should be two items", 2, items.size)
        assertEquals("The first item's name should be Milk", "Milk", items[0].name)
        assertEquals("The second item's name should be Banana", "Banana", items[1].name)

        collectJob.cancel()
    }
}
