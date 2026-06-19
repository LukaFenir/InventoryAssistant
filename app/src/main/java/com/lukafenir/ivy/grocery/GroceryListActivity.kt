package com.lukafenir.ivy.grocery

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lukafenir.ivy.R
import com.lukafenir.ivy.databinding.ActivityGroceryListBinding
import com.lukafenir.ivy.home.MainActivity
import com.lukafenir.ivy.settings.SettingsActivity
import kotlinx.coroutines.launch

class GroceryListActivity : AppCompatActivity() {

    private val viewModel: GroceryViewModel by viewModels {
        val local = RoomGroceryRepository(GroceryDatabase.getDatabase(this).groceryDao())
        val remote = FirestoreGroceryRepository()
        GroceryViewModelFactory(SyncedGroceryRepository(local, remote))
    }
    private lateinit var adapter: GroceryAdapter

    private lateinit var binding: ActivityGroceryListBinding

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val THEME_LIGHT = 0
        private const val THEME_DARK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupRecyclerView()
        setupSelectionMode()
        setupAddItem()
        setupDeleteSelectedItems()
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

        binding.navigationBar.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.navigationBar.listButton.isEnabled = false
    }

    private fun setupRecyclerView() {
        adapter = GroceryAdapter(
            onCheckedChanged = { item, isChecked -> viewModel.setChecked(item.id, isChecked) },
            onLongClick = { item -> if(!viewModel.isInSelectionMode.value) viewModel.toggleSelection(item.id) },
            onItemClick = { item -> viewModel.toggleSelection(item.id) }
        )
        binding.groceryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groceryRecyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allItems.collect { items ->
                    adapter.submitList(items)
                }
            }
        }
    }

    private fun setupSelectionMode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isInSelectionMode.collect { inSelectionMode ->
                    binding.normalHeader.visibility = if (inSelectionMode) View.GONE else View.VISIBLE
                    binding.selectionBar.visibility = if (inSelectionMode) View.VISIBLE else View.GONE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedIds.collect { ids ->
                    adapter.updateSelection(ids)
                    binding.selectionCountText.text = getString(R.string.items_selected, ids.size)
                }
            }
        }
    }

    private fun setupAddItem() {
        binding.addButton.setOnClickListener {
            val name = binding.itemNameInput.text.toString().trim()
            if(name.isNotBlank()) {
                viewModel.addItem(name)
                binding.itemNameInput.text.clear()
            }
        }
    }

    private fun setupDeleteSelectedItems() {
        binding.deleteSelectedButton.setOnClickListener {
            viewModel.deleteSelected()
        }

        binding.cancelSelectionButton.setOnClickListener {
            viewModel.clearSelection()
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

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getInt(KEY_THEME_MODE, THEME_LIGHT)

        when (savedTheme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}