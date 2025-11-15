package com.lukafenir.ivy

data class GroceryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val category: String = "Other"
)

sealed class InventoryItem {
    data class CategoryHeader(val category: String) : InventoryItem()
    data class GroceryItemData(val item: GroceryItem) : InventoryItem()
}

object SampleData {
    fun getSampleGroceries(): List<GroceryItem> {
        return listOf(
            GroceryItem(1, "Apples", 8, "Fruits"),
            GroceryItem(2, "Bananas", 6, "Fruits"),
            GroceryItem(3, "Milk", 2, "Dairy"),
            GroceryItem(4, "Bread", 1, "Bakery"),
            GroceryItem(5, "Eggs", 12, "Dairy"),
            GroceryItem(6, "Chicken Breast", 3, "Meat"),
            GroceryItem(7, "Rice", 1, "Grains"),
            GroceryItem(8, "Pasta", 2, "Grains"),
            GroceryItem(9, "Tomatoes", 5, "Vegetables"),
            GroceryItem(10, "Carrots", 10, "Vegetables"),
            GroceryItem(11, "Onions", 4, "Vegetables"),
            GroceryItem(12, "Cheese", 1, "Dairy"),
            GroceryItem(13, "Yogurt", 6, "Dairy"),
            GroceryItem(14, "Orange Juice", 1, "Beverages"),
            GroceryItem(15, "Cereal", 2, "Breakfast")
        )
    }
}