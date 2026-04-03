package com.lukafenir.ivy.grocery

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
    @DisplayName("WHEN insert called THEN item is inserted into local and remote")
    fun insert_writesToLocalAndRemote() = runTest {
        val item = GroceryItem(id = 1, name = "Milk")
        coEvery { local.insert(item) } returns Unit
        coEvery { remote.insert(item) } returns Unit

        repository.insert(item)

        coVerify { local.insert(item) }
        coVerify { remote.insert(item) }
    }

    // No tests for remote failures as the remote implementation handles retries internally
}
