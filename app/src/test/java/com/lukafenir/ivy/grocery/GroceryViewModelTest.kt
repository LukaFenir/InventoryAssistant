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
    @DisplayName("WHEN deleteSelected called THEN selected items are deleted from repository")
    fun deleteSelected_deletesSelectedItemsFromRepository() = runTest {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }

        viewModel.addItem("Milk")
        viewModel.addItem("Cheese")
        viewModel.addItem("Banana")

        assertEquals(3, viewModel.allItems.value.size, "Should be three items")

        val milkId = viewModel.allItems.value[0].id
        val bananaId = viewModel.allItems.value[2].id
        viewModel.toggleSelection(milkId)
        viewModel.toggleSelection(bananaId)
        viewModel.deleteSelected()

        val items = viewModel.allItems.value
        assertEquals(1, items.size, "Should be one item remaining")
        assertEquals("Cheese", items[0].name, "Only Cheese should remain")

        collectJob.cancel()
    }

    @Test
    @DisplayName("WHEN deleteSelected called THEN selection is cleared")
    fun deleteSelected_clearsSelection() = runTest {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }
        val selectionJob = launch(testDispatcher) { viewModel.selectedIds.collect {} }

        viewModel.addItem("Milk")
        viewModel.toggleSelection(viewModel.allItems.value[0].id)
        viewModel.deleteSelected()

        assertEquals(emptySet<Int>(), viewModel.selectedIds.value, "Selection should be empty after delete")

        collectJob.cancel()
        selectionJob.cancel()
    }

    @Test
    @DisplayName("WHEN clearSelected called THEN selected items are not deleted from repository")
    fun clearSelection_doesNotDeleteSelectedItems() = runTest {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }
        val selectionJob = launch(testDispatcher) { viewModel.selectedIds.collect {} }

        viewModel.addItem("Milk")
        viewModel.addItem("Cheese")
        viewModel.addItem("Banana")

        assertEquals(3, viewModel.allItems.value.size, "Should be three items")

        val milkId = viewModel.allItems.value[0].id
        val bananaId = viewModel.allItems.value[2].id
        viewModel.toggleSelection(milkId)
        viewModel.toggleSelection(bananaId)
        viewModel.clearSelection()

        assertEquals(3, viewModel.allItems.value.size, "Should be three items still")

        collectJob.cancel()
        selectionJob.cancel()
    }

    @Test
    @DisplayName("WHEN clearSelected called THEN selection is cleared")
    fun clearSelection_clearsSelection() = runTest {
        val collectJob = launch(testDispatcher) { viewModel.allItems.collect {} }
        val selectionJob = launch(testDispatcher) { viewModel.selectedIds.collect {} }

        viewModel.addItem("Milk")
        viewModel.toggleSelection(viewModel.allItems.value[0].id)

        viewModel.clearSelection()

        assertEquals(emptySet<Int>(), viewModel.selectedIds.value, "Selection should be empty after delete")

        collectJob.cancel()
        selectionJob.cancel()
    }

}
