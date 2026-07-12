package com.lukafenir.ivy.grocery

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

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
    @DisplayName("WHEN viewModel initialised THEN allItems is empty")
    fun viewModelInitialised_allItemsIsEmpty() = runTest {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        val items = viewModel.allItems.value
        assertEquals(0, items.size, "Should be zero items")

        collectJob.cancel()
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

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "\t", "\n"])
    @DisplayName("WHEN addItem called with empty string THEN doesn't insert into repository")
    fun addItemWithEmptyString_doesNotInsertItemIntoRepository(emptyString: String) = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem(emptyString)

        val items = viewModel.allItems.value
        assertEquals(0, items.size, "Should be no items")

        collectJob.cancel()
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("whitespaceArguments")
    @DisplayName("WHEN addItem called with whitespace THEN insert into repository stripped of whitespace")
    fun addItemWithTrailingWhitespace_insertsIntoRepositoryStrippedOfWhitespace(withWhitespace: String, expected: String) = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem(withWhitespace)

        val items = viewModel.allItems.value
        assertEquals(1, items.size, "Should be one item")
        assertEquals(expected, items[0].name, "Item name should be stripped of whitespace")

        collectJob.cancel()
    }

    companion object {
        @JvmStatic
        fun whitespaceArguments() = listOf(
            Arguments.of(" Milk", "Milk"),
            Arguments.of(" Cheese  ", "Cheese"),
            Arguments.of(" Banana ", "Banana"),
            Arguments.of("Cake            ", "Cake"),
        )
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
    @DisplayName("WHEN setChecked called on checked item THEN item is unchecked")
    fun setCheckedOnAlreadyChecked_updatesItemInRepositoryToUnchecked() = runTest {
        // Start a subscriber so WhileSubscribed activates the upstream Flow
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")
        viewModel.setChecked(viewModel.allItems.value[0].id, true)

        viewModel.setChecked(viewModel.allItems.value[0].id, false)

        val items = viewModel.allItems.value
        assertEquals(1, items.size, "Should be one item")
        assertEquals(false, items[0].isChecked, "The item should be unchecked")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN management mode entered THEN isInManagementMode is true")
    fun enterManagementMode_setsIsInManagementModeTrue() {
        viewModel.enterManagementMode()
        assertTrue(viewModel.isInManagementMode.value)
    }

    @Test
    @DisplayName("WHEN management mode is entered AND exited THEN isInManagementMode is false")
    fun enterThenExitManagementMode_setsIsInManagementModeFalse() {
        viewModel.enterManagementMode()
        viewModel.exitManagementMode()
        assertFalse(viewModel.isInManagementMode.value)
    }

    @Test
    @DisplayName("WHEN management mode exited THEN isInManagementMode is false")
    fun exitManagementMode_setsIsInManagementModeFalse() {
        viewModel.exitManagementMode()
        assertFalse(viewModel.isInManagementMode.value)
    }

    @Test
    @DisplayName("WHEN deleteItem called THEN item is deleted from repository")
    fun deleteItem_deletesItemFromRepository() = runTest {
        val collectJob = setupDeleteTests()

        viewModel.deleteItem(viewModel.allItems.value[2])

        val items = viewModel.allItems.value
        assertEquals(2, items.size, "Should be two items left after delete")
        assertEquals("Milk", items[0].name, "Should be left with Milk")
        assertEquals("Cheese", items[1].name, "Should be left with Cheese")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteItem called on all items THEN all items are deleted from repository")
    fun deleteItemOnAllItems_deletesAllItemFromRepository() = runTest {
        val collectJob = setupDeleteTests()

        //Delete the first item in the list 3 times
        viewModel.deleteItem(viewModel.allItems.value[0])
        viewModel.deleteItem(viewModel.allItems.value[0])
        viewModel.deleteItem(viewModel.allItems.value[0])

        assertEquals(0, viewModel.allItems.value.size, "Should be no items left after multiple deletes")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteItem called twice on same id THEN item is deleted once")
    fun deleteItemTwiceOnSameId_deletesItemOnce() = runTest {
        val collectJob = setupDeleteTests()

        var item = viewModel.allItems.value[0]
        viewModel.deleteItem(item)
        viewModel.deleteItem(item)

        assertEquals(2, viewModel.allItems.value.size, "Should still be 2 items after deleting same item twice")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteItem called on id that doesn't exist THEN no item is deleted")
    fun deleteItemOnNonexistentId_noItemIsDeleted() = runTest {
        val collectJob = setupDeleteTests()

        var nonexistentItem = GroceryItem(77, "Non-Existent Item")
        viewModel.deleteItem(nonexistentItem)

        assertEquals(3, viewModel.allItems.value.size, "Should still be 3 items after deleting an item that doesn't exist")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteItem called on multiple items THEN deletes are initiated concurrently")
    fun deleteItem_multipleCallsInitiatedConcurrently() = runTest {
        val collectJob = setupDeleteTests()
        repository.shouldHangOnDelete = true
        val firstItem = viewModel.allItems.value[0]
        val secondItem = viewModel.allItems.value[2]

        viewModel.deleteItem(firstItem)
        viewModel.deleteItem(secondItem)
        advanceUntilIdle()

        assertEquals(2, repository.deleteCallCount, "Expected two calls to delete even though they hang")
        collectJob.cancel()
    }

    private fun CoroutineScope.setupDeleteTests(): Job {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }
        viewModel.addItem("Milk")
        viewModel.addItem("Cheese")
        viewModel.addItem("Banana")
        assertEquals(3, viewModel.allItems.value.size, "Should be three items")
        return collectJob
    }
}
