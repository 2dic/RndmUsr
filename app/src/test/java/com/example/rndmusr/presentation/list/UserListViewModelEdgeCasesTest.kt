package com.example.rndmusr.presentation.list

import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.mockk
import io.mockk.every
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import com.example.rndmusr.utils.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelEdgeCasesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: UserListViewModel
    private val mockRepository = mockk<UserRepository>()

    @Test
    fun `should handle repository errors gracefully and show error state`() = runTest {
        val errorMessage = "Database error"
        every { mockRepository.getUsers() } returns flow {
            throw Exception(errorMessage)
        }

        viewModel = UserListViewModel(mockRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Should be Error state, but was: $state", state is UserListUiState.Error)
        assertEquals(errorMessage, (state as UserListUiState.Error).message)
    }

    @Test
    fun `should handle empty list after having users`() = runTest {
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)

        val initialState = viewModel.uiState.value
        assertTrue("Should start with Loading", initialState is UserListUiState.Loading)

        usersFlow.emit(listOf(createTestUser()))
        advanceUntilIdle()

        val successState = viewModel.uiState.value
        assertTrue("Should be Success", successState is UserListUiState.Success)

        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val emptyState = viewModel.uiState.value
        assertTrue("Should transition to Empty", emptyState is UserListUiState.Empty)
    }

    @Test
    fun `should handle large list of users`() = runTest {
        val largeUserList = (1..1000).map { createTestUser(id = it.toString()) }
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)
        usersFlow.emit(largeUserList)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Should handle large list", state is UserListUiState.Success)
        assertEquals(1000, (state as UserListUiState.Success).users.size)
    }

    @Test
    fun `should handle rapid state changes`() = runTest {
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)

        usersFlow.emit(listOf(createTestUser("1")))
        usersFlow.emit(emptyList())
        usersFlow.emit(listOf(createTestUser("2"), createTestUser("3")))
        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertTrue("Should be in Empty state after rapid changes",
            finalState is UserListUiState.Empty)
    }

    @Test
    fun `should show loading state initially`() = runTest {
        every { mockRepository.getUsers() } returns flow {
        }

        viewModel = UserListViewModel(mockRepository)

        val initialState = viewModel.uiState.value
        assertTrue("Should be Loading initially, but was: $initialState",
            initialState is UserListUiState.Loading)
    }

    @Test
    fun `should transition through loading to success state`() = runTest {
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)

        val loadingState = viewModel.uiState.value
        assertTrue("Should be Loading initially, but was: $loadingState",
            loadingState is UserListUiState.Loading)

        val testUsers = listOf(createTestUser("1"))
        usersFlow.emit(testUsers)
        advanceUntilIdle()

        val successState = viewModel.uiState.value
        assertTrue("Should be Success after data, but was: $successState",
            successState is UserListUiState.Success)
        assertEquals(testUsers, (successState as UserListUiState.Success).users)
    }

    @Test
    fun `should handle duplicate users`() = runTest {
        val duplicateUser = createTestUser("1")
        val usersWithDuplicates = listOf(duplicateUser, duplicateUser)
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)
        usersFlow.emit(usersWithDuplicates)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Should handle duplicates", state is UserListUiState.Success)
        assertEquals(2, (state as UserListUiState.Success).users.size)
    }

    @Test
    fun `should handle users with empty fields`() = runTest {
        val userWithEmptyFields = User(
            id = "1",
            gender = "",
            title = "",
            firstName = "",
            lastName = "",
            email = "",
            phone = "",
            cell = "",
            picture = "",
            nationality = "",
            street = "",
            city = "",
            state = "",
            country = "",
            postcode = "",
            latitude = "",
            longitude = "",
            timezoneOffset = "",
            timezoneDescription = "",
            age = 0,
            birthDate = ""
        )
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)
        usersFlow.emit(listOf(userWithEmptyFields))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Should handle users with empty fields", state is UserListUiState.Success)
        assertEquals(1, (state as UserListUiState.Success).users.size)
    }

    @Test
    fun `should handle empty state correctly`() = runTest {
        val usersFlow = MutableSharedFlow<List<User>>()
        every { mockRepository.getUsers() } returns usersFlow

        viewModel = UserListViewModel(mockRepository)

        val loadingState = viewModel.uiState.value
        assertTrue("Should be Loading initially", loadingState is UserListUiState.Loading)

        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val emptyState = viewModel.uiState.value
        assertTrue("Should be Empty after emitting empty list", emptyState is UserListUiState.Empty)
    }

    private fun createTestUser(id: String = "1"): User {
        return User(
            id = id,
            gender = "male",
            title = "Mr",
            firstName = "John",
            lastName = "Doe",
            email = "john$id@example.com",
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