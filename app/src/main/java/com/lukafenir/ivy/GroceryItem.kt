package com.lukafenir.ivy

data class GroceryItem(
    val id: Int,
    val name: String,
    val isChecked: Boolean = false
)

object SampleData {
    fun getSampleGroceries(): List<GroceryItem> {
        return listOf(
            GroceryItem(1, "Apples"),
            GroceryItem(2, "Bananas"),
            GroceryItem(3, "Milk"),
            GroceryItem(4, "Bread"),
            GroceryItem(5, "Eggs"),
            GroceryItem(6, "Chicken Breast"),
            GroceryItem(7, "Rice")
        )
    }
}