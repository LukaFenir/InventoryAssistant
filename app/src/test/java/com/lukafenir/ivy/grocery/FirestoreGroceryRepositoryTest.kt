package com.lukafenir.ivy.grocery

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

        val insertedId = repository.insert(item)

        verify { itemDocument.set(mapOf("name" to "Milk", "isChecked" to false)) }
        assertEquals(1, insertedId)
    }

    @Test
    @DisplayName("GIVEN call to firestore fails WHEN insert called THEN exception is thrown")
    fun insert_throwsExceptionOnFirestoreFailure() = runTest {
        every { itemsCollection.document("1") } returns itemDocument
        every { itemDocument.set(any()) } returns voidTask
        every { voidTask.exception } returns FirebaseException("Firestore error")

        assertThrows<FirebaseException> {
            repository.insert(GroceryItem(id = 1, name = "Milk"))
        }
    }

    @Test
    @DisplayName("WHEN delete called THEN item is removed from Firestore")
    fun delete_removesItemFromFirestore() = runTest {
        val (_, itemToDelete, _) = setupItems()
        every { itemDocument.delete() } returns voidTask

        repository.delete(itemToDelete)

        verify { itemDocument.delete() }
    }

    @Test
    @DisplayName("GIVEN call to firestore fails WHEN delete called THEN exception is thrown")
    fun delete_throwsExceptionOnFirestoreFailure() = runTest {
        every { itemsCollection.document("1") } returns itemDocument
        every { itemDocument.delete() } returns voidTask
        every { voidTask.exception } returns FirebaseException("Firestore error")

        assertThrows<FirebaseException> {
            repository.delete(GroceryItem(id = 1, name = "Milk"))
        }
    }

    @Test
    @DisplayName("WHEN setChecked called THEN item is checked in Firestore")
    fun setChecked_checksItemInFirestore() = runTest {
        val (_, itemToCheck, _) = setupItems()
        every { itemDocument.update(mapOf("isChecked" to true)) } returns voidTask

        repository.setChecked(itemToCheck.id, true)

        verify { itemDocument.update(mapOf("isChecked" to true)) }
    }

    private suspend fun setupItems(): List<GroceryItem> {
        val items = listOf(
            GroceryItem(id = 1, name = "Milk"),
            GroceryItem(id = 2, name = "Eggs"),
            GroceryItem(id = 3, name = "Bread")
        )
        every { itemsCollection.document(any()) } returns itemDocument
        every { itemDocument.set(any()) } returns voidTask
        items.forEach { repository.insert(it) }
        return items
    }
}
