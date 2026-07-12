package com.lukafenir.ivy.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lukafenir.ivy.R

class GroceryAdapter(
    private val onCheckedChanged: (GroceryItem, Boolean) -> Unit,
    private val onLongClick: (GroceryItem) -> Unit,
    private val onManagementLongClick: (GroceryItem) -> Unit,
    private val onDeleteItem: (GroceryItem) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.ViewHolder>(GroceryDiffCallback()) {

    private var isInManagementMode: Boolean = false
    private var editingItemId: Int? = null

    fun setManagementMode(inMode: Boolean){
        isInManagementMode = inMode
        notifyDataSetChanged()
    }

    fun setEditingItem(id: Int?){
        editingItemId = id
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.groceryName.text = item.name

        if (isInManagementMode){
            holder.groceryCheckBox.setOnCheckedChangeListener(null)
            holder.itemView.setOnLongClickListener(null)
            holder.itemView.isLongClickable = false
            holder.groceryCheckBox.visibility = View.INVISIBLE
            holder.groceryDeleteButton.visibility = View.VISIBLE
            holder.groceryDeleteButton.setOnClickListener { onDeleteItem(item) }
            holder.groceryName.setOnLongClickListener { onManagementLongClick(item); true }
            if(item.id == editingItemId) {
                holder.groceryNameEdit.setText(item.name)
                holder.groceryNameEdit.visibility = View.VISIBLE
                holder.groceryName.visibility = View.GONE
            } else {
                holder.groceryName.visibility = View.VISIBLE
                holder.groceryNameEdit.visibility = View.GONE
                holder.groceryNameEdit.setText(null)
            }
        } else {
            holder.groceryCheckBox.setOnCheckedChangeListener(null)
            holder.groceryName.setOnLongClickListener(null)
            holder.groceryName.isLongClickable = false
            holder.groceryNameEdit.visibility = View.GONE
            holder.groceryName.visibility = View.VISIBLE
            holder.groceryCheckBox.visibility = View.VISIBLE
            holder.groceryCheckBox.isChecked = item.isChecked
            holder.groceryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(item, isChecked)
            }
            holder.itemView.setOnLongClickListener { onLongClick(item); true }
            holder.groceryDeleteButton.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groceryName: TextView = itemView.findViewById(R.id.groceryName)
        val groceryCheckBox: CheckBox = itemView.findViewById(R.id.groceryCheckBox)
        val groceryDeleteButton: MaterialButton = itemView.findViewById(R.id.groceryDeleteButton)
        val groceryNameEdit: EditText = itemView.findViewById(R.id.groceryNameEdit)
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
