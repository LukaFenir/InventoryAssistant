package com.lukafenir.ivy.grocery

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.lukafenir.ivy.R

class GroceryAdapter(
    private val onCheckedChanged: (GroceryItem, Boolean) -> Unit,
    private val onLongClick: (GroceryItem) -> Unit,
    private val onItemClick: (GroceryItem) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.ViewHolder>(GroceryDiffCallback()) {

    private var selectedIds: Set<Int> = emptySet()
    private var isInSelectionMode: Boolean = false

    fun updateSelection(ids: Set<Int>){
        selectedIds = ids
        isInSelectionMode = ids.isNotEmpty()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.groceryName.text = item.name

        val isSelected = item.id in selectedIds
        val bgColor = if (isSelected) {
            MaterialColors.getColor(holder.itemView, androidx.appcompat.R.attr.colorPrimary).let {
                Color.argb(51, Color.red(it), Color.green(it), Color.blue(it))
            }
        } else {
            Color.TRANSPARENT
        }
        holder.itemView.setBackgroundColor(bgColor)

        if (isInSelectionMode) {
            holder.groceryCheckBox.setOnCheckedChangeListener(null)
            holder.groceryCheckBox.isChecked = isSelected
            holder.itemView.setOnClickListener { onItemClick(item) }
            holder.itemView.setOnLongClickListener { onLongClick(item); true }
        } else {
            holder.groceryCheckBox.setOnCheckedChangeListener(null)
            holder.groceryCheckBox.isChecked = item.isChecked
            holder.groceryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(item, isChecked)
            }
            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnLongClickListener { onLongClick(item); true }
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