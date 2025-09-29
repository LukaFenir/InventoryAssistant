package com.lukafenir.ivy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroceryAdapter(private var groceries: List<GroceryItem>) :
    RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.groceryName)
        val quantityTextView: TextView = itemView.findViewById(R.id.groceryQuantity)
        val categoryTextView: TextView = itemView.findViewById(R.id.groceryCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val grocery = groceries[position]
        holder.nameTextView.text = grocery.name
        holder.quantityTextView.text = grocery.quantity.toString()
        holder.categoryTextView.text = grocery.category
    }

    override fun getItemCount(): Int = groceries.size

    fun updateGroceries(newGroceries: List<GroceryItem>) {
        groceries = newGroceries
        notifyDataSetChanged()
    }
}