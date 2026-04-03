package com.lukafenir.ivy.grocery

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
}
