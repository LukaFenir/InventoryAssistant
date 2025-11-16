package com.lukafenir.ivy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class InventoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var groceryAdapter: GroceryAdapter
    private lateinit var homeButton: MaterialButton
    private lateinit var settingsButton: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val THEME_LIGHT = 0
        private const val THEME_DARK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        initializeViews()
        setupRecyclerView()
        setupNavigation()
        loadInventory()
        disableTransition()
    }

    override fun finish() {
        super.finish()
        disableTransition()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.inventoryRecyclerView)
        homeButton = findViewById(R.id.homeButton)
        settingsButton = findViewById(R.id.settingsButton)
        val inventoryButton: MaterialButton = findViewById(R.id.inventoryButton)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Disable inventory button since we're on the inventory screen
        inventoryButton.isEnabled = false
    }

    private fun setupRecyclerView() {
        groceryAdapter = GroceryAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
            adapter = groceryAdapter
        }
    }

    private fun setupNavigation() {
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun disableTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun loadInventory() {
        val groceries = SampleData.getSampleGroceries()
        val groupedItems = groupItemsByCategory(groceries)
        groceryAdapter.updateItems(groupedItems)
    }

    private fun groupItemsByCategory(groceries: List<GroceryItem>): List<InventoryItem> {
        val result = mutableListOf<InventoryItem>()
        val groupedByCategory = groceries.groupBy { it.category }

        groupedByCategory.forEach { (category, items) ->
            result.add(InventoryItem.CategoryHeader(category))
            items.forEach { item ->
                result.add(InventoryItem.GroceryItemData(item))
            }
        }

        return result
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getInt(KEY_THEME_MODE, THEME_LIGHT)

        when (savedTheme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}