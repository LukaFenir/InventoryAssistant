package com.lukafenir.ivy.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukafenir.ivy.R

class GroceryAdapter(private val groceryItemList: List<GroceryItem>) : RecyclerView.Adapter<GroceryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groceryItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groceryItem = groceryItemList[position]

        holder.groceryName.text = groceryItem.name
        holder.groceryCheckBox.isChecked = groceryItem.isChecked
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groceryName: TextView = itemView.findViewById(R.id.groceryName)
        val groceryCheckBox: CheckBox = itemView.findViewById(R.id.groceryCheckBox)
    }
}