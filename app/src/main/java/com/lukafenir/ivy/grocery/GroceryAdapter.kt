package com.lukafenir.ivy.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lukafenir.ivy.R

class GroceryAdapter(
    private val onCheckedChanged: (GroceryItem, Boolean) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.ViewHolder>(GroceryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.groceryName.text = item.name

        holder.groceryCheckBox.setOnCheckedChangeListener(null)
        holder.groceryCheckBox.isChecked = item.isChecked
        holder.groceryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChanged(item, isChecked)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groceryName: TextView = itemView.findViewById(R.id.groceryName)
        val groceryCheckBox: CheckBox = itemView.findViewById(R.id.groceryCheckBox)
    }
}

class GroceryDiffCallback : DiffUtil.ItemCallback<GroceryItem>() {
    override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
        return oldItem == newItem
    }

}