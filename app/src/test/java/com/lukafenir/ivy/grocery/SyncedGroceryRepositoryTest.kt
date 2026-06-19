package com.lukafenir.ivy.grocery

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SyncedGroceryRepositoryTest {

    private val local = mockk<GroceryRepository>()
    private val remote = mockk<GroceryRepository>()

    private lateinit var repository: SyncedGroceryRepository

    @BeforeEach
    fun setup() {
        repository = SyncedGroceryRepository(local, remote)
    }

    @Test
    @DisplayName("WHEN insert called THEN item is inserted into local and remote with the local-generated ID")
    fun insert_writesToLocalAndRemoteWithGeneratedId() = runTest {
        val item = GroceryItem(name = "Milk") // id defaults to 0
        val generatedId = 42L
        coEvery { local.insert(item) } returns generatedId
        coEvery { remote.insert(item.copy(id = generatedId.toInt())) } returns generatedId

        val insertedId = repository.insert(item)

        coVerify { local.insert(item) }
        coVerify { remote.insert(item.copy(id = generatedId.toInt())) }
        assertEquals(generatedId, insertedId)

    }

    // No tests for remote failures as the remote implementation handles retries internally

    @Test
    @DisplayName("WHEN delete called THEN item is deleted from local and remote")
    fun delete_removedFromLocalAndRemote() = runTest {
        val (_, itemToDelete, _) = setupItems()
        coEvery { local.delete(itemToDelete) } just runs
        coEvery { remote.delete(itemToDelete) } just runs

        repository.delete(itemToDelete)

        coVerify { local.delete(itemToDelete) }
        coVerify { remote.delete(itemToDelete) }
    }

    @Test
    @DisplayName("WHEN local delete fails THEN remote delete is not called")
    fun delete_localFailure_doesNotDeleteFromRemote() = runTest {
        val (_, itemToDelete, _) = setupItems()
        coEvery { local.delete(itemToDelete) } throws RuntimeException("DB error")

        runCatching { repository.delete(itemToDelete) }

        coVerify(exactly = 0) { remote.delete(any()) }
    }

    @Test
    @DisplayName("WHEN setChecked called THEN item is checked in local and remote")
    fun setChecked_checkInLocalAndRemote() = runTest {
        val (_, itemToCheck, _) = setupItems()
        coEvery { local.setChecked(itemToCheck.id, true) } just runs

        repository.setChecked(itemToCheck.id, true)

        coVerify { local.setChecked(itemToCheck.id, true) }
        coVerify { remote.setChecked(itemToCheck.id, true) }
    }

    private suspend fun setupItems(): List<GroceryItem> {
        val item1 = GroceryItem(name = "Milk")
        val item2 = GroceryItem(name = "Eggs")
        val item3 = GroceryItem(name = "Bread")
        coEvery { local.insert(item1) } returns 1L
        coEvery { remote.insert(item1.copy(id = 1)) } returns 1L
        coEvery { local.insert(item2) } returns 2L
        coEvery { remote.insert(item2.copy(id = 2)) } returns 2L
        coEvery { local.insert(item3) } returns 3L
        coEvery { remote.insert(item3.copy(id = 3)) } returns 3L
        repository.insert(item1)
        repository.insert(item2)
        repository.insert(item3)
        return listOf(item1.copy(id = 1), item2.copy(id = 2), item3.copy(id = 3))
    }
}
