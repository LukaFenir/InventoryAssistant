package com.lukafenir.ivy.grocery

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryViewModelTest {

    private lateinit var repository: FakeGroceryRepository
    private lateinit var viewModel: GroceryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeGroceryRepository()
        viewModel = GroceryViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("WHEN addItem called THEN inserts item into repository")
    fun addItem_insertsItemIntoRepository() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")

        val items = viewModel.allItems.value
        assertEquals(1, items.size, "Should be one item")
        assertEquals("Milk", items[0].name, "The item's name should be Milk")
        assertEquals(false, items[0].isChecked, "The item should be unchecked")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN setChecked called THEN item is checked")
    fun setChecked_updatesItemInRepositoryToChecked() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")

        viewModel.setChecked(viewModel.allItems.value[0].id, true)

        val items = viewModel.allItems.value
        assertEquals(1, items.size, "Should be one item")
        assertEquals(true, items[0].isChecked, "The item should be checked")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteItem called THEN item is deleted from repository")
    fun deleteItem_deletesItemFromRepository() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")
        viewModel.addItem("Cheese")
        viewModel.addItem("Banana")

        assertEquals(3, viewModel.allItems.value.size, "Should be three items")

        viewModel.deleteItem(viewModel.allItems.value[1])
        val items = viewModel.allItems.value
        assertEquals(2, items.size, "Should be two items")
        assertEquals("Milk", items[0].name, "The first item's name should be Milk")
        assertEquals("Banana", items[1].name, "The second item's name should be Banana")

        collectJob.cancel()
    }
}
