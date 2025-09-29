package com.lukafenir.ivy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.inventoryRecyclerView)
        homeButton = findViewById(R.id.homeButton)
        settingsButton = findViewById(R.id.settingsButton)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadInventory() {
        val groceries = SampleData.getSampleGroceries()
        groceryAdapter.updateGroceries(groceries)
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