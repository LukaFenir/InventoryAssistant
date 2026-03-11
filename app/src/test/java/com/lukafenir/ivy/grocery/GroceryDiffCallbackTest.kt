package com.lukafenir.ivy.grocery

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GroceryDiffCallbackTest {

    private val diffCallback = GroceryDiffCallback()

    @Test
    @DisplayName("WHEN two items have the same id THEN areItemsTheSame returns true")
    fun areItemsTheSame_sameId_returnsTrue() {
        val item = GroceryItem(id = 1, name = "Milk")
        assertTrue(diffCallback.areItemsTheSame(item, item.copy(name = "Cheese")))
    }

    @Test
    @DisplayName("WHEN two items have different ids THEN areItemsTheSame returns false")
    fun areItemsTheSame_differentId_returnsFalse() {
        val item1 = GroceryItem(id = 1, name = "Milk")
        val item2 = GroceryItem(id = 2, name = "Milk")
        assertFalse(diffCallback.areItemsTheSame(item1, item2))
    }

    @Test
    @DisplayName("WHEN two items are identical THEN areContentsTheSame returns true")
    fun areContentsTheSame_identicalItems_returnsTrue() {
        val item = GroceryItem(id = 1, name = "Milk", isChecked = false)
        assertTrue(diffCallback.areContentsTheSame(item, item.copy()))
    }

    @Test
    @DisplayName("WHEN two items have different names THEN areContentsTheSame returns false")
    fun areContentsTheSame_differentName_returnsFalse() {
        val item1 = GroceryItem(id = 1, name = "Milk")
        val item2 = GroceryItem(id = 1, name = "Cheese")
        assertFalse(diffCallback.areContentsTheSame(item1, item2))
    }

    @Test
    @DisplayName("WHEN two items have different checked state THEN areContentsTheSame returns false")
    fun areContentsTheSame_differentCheckedState_returnsFalse() {
        val item1 = GroceryItem(id = 1, name = "Milk", isChecked = false)
        val item2 = GroceryItem(id = 1, name = "Milk", isChecked = true)
        assertFalse(diffCallback.areContentsTheSame(item1, item2))
    }
}
