package com.lukafenir.ivy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var themeToggleButton: MaterialButton
    private lateinit var homeButton: MaterialButton
    private lateinit var inventoryButton: MaterialButton

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val THEME_LIGHT = 0
        private const val THEME_DARK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setupNavigation()
        setupThemeToggle()
    }

    private fun initializeViews() {
        themeToggleButton = findViewById(R.id.themeToggleButton)
        homeButton = findViewById(R.id.homeButton)
        inventoryButton = findViewById(R.id.inventoryButton)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        updateThemeButtonText()
    }

    private fun setupNavigation() {
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        inventoryButton.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupThemeToggle() {
        themeToggleButton.setOnClickListener {
            toggleTheme()
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getInt(KEY_THEME_MODE, THEME_LIGHT)

        when (savedTheme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun toggleTheme() {
        val currentTheme = sharedPreferences.getInt(KEY_THEME_MODE, THEME_LIGHT)
        val newTheme = if (currentTheme == THEME_LIGHT) THEME_DARK else THEME_LIGHT

        sharedPreferences.edit()
            .putInt(KEY_THEME_MODE, newTheme)
            .apply()

        when (newTheme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun updateThemeButtonText() {
        val currentTheme = sharedPreferences.getInt(KEY_THEME_MODE, THEME_LIGHT)
        val buttonText = if (currentTheme == THEME_LIGHT) {
            getString(R.string.switch_to_dark)
        } else {
            getString(R.string.switch_to_light)
        }
        themeToggleButton.text = buttonText
    }

    override fun onResume() {
        super.onResume()
        updateThemeButtonText()
    }
}