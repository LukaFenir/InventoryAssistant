package com.lukafenir.ivy.grocery

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreGroceryRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ) : GroceryRepository {

    private val listId = "home-list"

    private val itemsCollection
        get() = firestore.collection("lists").document(listId).collection("items")

    override val allItems: Flow<List<GroceryItem>> = callbackFlow {
    }

    override suspend fun insert(item: GroceryItem) : Long {
        itemsCollection.document(item.id.toString()).set(item.toMap()).await()
        return item.id.toLong()
    }

    override suspend fun update(item: GroceryItem) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: GroceryItem) {
        itemsCollection.document(item.id.toString()).delete().await()
    }

    override suspend fun setChecked(id: Int, isChecked: Boolean) {
        TODO("Not yet implemented")
    }

    private fun GroceryItem.toMap() = mapOf(
        "name" to name,
        "isChecked" to isChecked
    )

}