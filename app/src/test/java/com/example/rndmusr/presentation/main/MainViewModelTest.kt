// test/presentation/main/SimpleMainViewModelTest.kt
package com.example.rndmusr.presentation.main

import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import com.example.rndmusr.utils.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleMainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    private val mockRepository = mockk<UserRepository>()

    @Before
    fun setUp() {
        viewModel = MainViewModel(mockRepository)
    }

    @Test
    fun `should start with idle state`() = runTest {
        // Then
        assertTrue(viewModel.uiState.value is MainUiState.Idle)
    }

    @Test
    fun `should update state to Loading then Success when generating user`() = runTest {
        // Given
        val testUser = createTestUser()
        coEvery { mockRepository.getRandomUser(any(), any()) } returns Result.success(testUser)

        // When
        viewModel.generateUser("male", "US")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is MainUiState.Success)
        assertEquals(testUser, (state as MainUiState.Success).user)
    }

    @Test
    fun `should handle error when repository fails`() = runTest {
        // Given
        coEvery { mockRepository.getRandomUser(any(), any()) } returns
                Result.failure(Exception("Network error"))

        // When
        viewModel.generateUser()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is MainUiState.Error)
        assertEquals("Network error", (state as MainUiState.Error).message)
    }

    @Test
    fun `should save user when saveUser is called`() = runTest {
        // Given
        val testUser = createTestUser()
        coEvery { mockRepository.saveUser(testUser) } returns Unit

        // When
        viewModel.saveUser(testUser)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.saveUser(testUser) }
    }

    @Test
    fun `should reset state to idle`() = runTest {
        // When
        viewModel.resetState()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is MainUiState.Idle)
    }

    private fun createTestUser(): User {
        return User(
            id = "test-id",
            gender = "male",
            title = "Mr",
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            phone = "123456789",
            cell = "123456789",
            picture = "https://example.com/photo.jpg",
            nationality = "US",
            street = "123 Main St",
            city = "New York",
            state = "NY",
            country = "USA",
            postcode = "10001",
            latitude = "40.7128",
            longitude = "-74.0060",
            timezoneOffset = "-5:00",
            timezoneDescription = "EST",
            age = 30,
            birthDate = "1993-05-15T00:00:00.000Z"
        )
    }
}