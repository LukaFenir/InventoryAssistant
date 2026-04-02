package com.lukafenir.ivy.grocery

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FirestoreGroceryRepositoryTest {

    private val firestore = mockk<FirebaseFirestore>()
    private val listsCollection = mockk<CollectionReference>()
    private val listDocument = mockk<DocumentReference>()
    private val itemsCollection = mockk<CollectionReference>()
    private val itemDocument = mockk<DocumentReference>()
    private val voidTask = mockk<Task<Void>>()

    private lateinit var repository: FirestoreGroceryRepository

    @BeforeEach
    fun setup() {
        every { firestore.collection("lists") } returns listsCollection
        every { listsCollection.document("home-list") } returns listDocument
        every { listDocument.collection("items") } returns itemsCollection

        // Make Task complete immediately so await() short-circuits
        every { voidTask.isComplete } returns true
        every { voidTask.isCanceled } returns false
        every { voidTask.exception } returns null
        every { voidTask.result } returns null

        repository = FirestoreGroceryRepository(firestore)
    }

    @Test
    @DisplayName("WHEN insert called THEN item is written to Firestore with correct data")
    fun insert_writesItemToFirestore() = runTest {
        val item = GroceryItem(id = 1, name = "Milk")
        every { itemsCollection.document("1") } returns itemDocument
        every { itemDocument.set(any()) } returns voidTask

        repository.insert(item)

        verify { itemDocument.set(mapOf("name" to "Milk", "isChecked" to false)) }
    }
}
