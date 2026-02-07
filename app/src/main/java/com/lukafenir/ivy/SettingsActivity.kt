package com.lukafenir.ivy

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.lukafenir.ivy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val THEME_LIGHT = 0
        private const val THEME_DARK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupThemeToggle()
        disableTransition()
    }

    override fun finish() {
        super.finish()
        disableTransition()
    }

    private fun setupNavigation() {
        binding.navigationBar.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.navigationBar.listButton.setOnClickListener {
            val intent = Intent(this, GroceryListActivity::class.java)
            startActivity(intent)
        }

        binding.navigationBar.settingsButton.isEnabled = false
    }

    private fun disableTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun setupThemeToggle() {
        val currentTheme = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_THEME_MODE, THEME_LIGHT)
        binding.themeToggleButton.text = if (currentTheme == THEME_LIGHT) {
            getString(R.string.switch_to_dark)
        } else {
            getString(R.string.switch_to_light)
        }
        binding.themeToggleButton.setOnClickListener {
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
        val currentTheme = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_THEME_MODE, THEME_LIGHT)
        val newTheme = if (currentTheme == THEME_LIGHT) THEME_DARK else THEME_LIGHT

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putInt(KEY_THEME_MODE, newTheme)
        }

        when (newTheme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}