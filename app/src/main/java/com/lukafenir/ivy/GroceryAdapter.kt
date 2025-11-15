package com.lukafenir.ivy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroceryAdapter(private var items: List<InventoryItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CATEGORY_HEADER = 0
        private const val VIEW_TYPE_GROCERY_ITEM = 1
    }

    class CategoryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
    }

    class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.groceryName)
        val quantityTextView: TextView = itemView.findViewById(R.id.groceryQuantity)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is InventoryItem.CategoryHeader -> VIEW_TYPE_CATEGORY_HEADER
            is InventoryItem.GroceryItemData -> VIEW_TYPE_GROCERY_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
                CategoryHeaderViewHolder(view)
            }
            VIEW_TYPE_GROCERY_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grocery, parent, false)
                GroceryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is InventoryItem.CategoryHeader -> {
                (holder as CategoryHeaderViewHolder).categoryTitle.text = item.category
            }
            is InventoryItem.GroceryItemData -> {
                val groceryHolder = holder as GroceryViewHolder
                groceryHolder.nameTextView.text = item.item.name
                groceryHolder.quantityTextView.text = item.item.quantity.toString()

                // Set up long-click listener to show popup menu
                groceryHolder.itemView.setOnLongClickListener { view ->
                    showPopupMenu(view, item.item)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<InventoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, item: GroceryItem) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.grocery_item_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // TODO: Implement edit functionality
                    true
                }
                R.id.action_delete -> {
                    // TODO: Implement delete functionality
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}