package com.example.rndmusr.presentation.list

import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import com.example.rndmusr.utils.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: UserListViewModel
    private val mockRepository = mockk<UserRepository>()

    private val usersFlow = MutableSharedFlow<List<User>>()

    @Before
    fun setUp() {
        every { mockRepository.getUsers() } returns usersFlow
    }

    @Test
    fun `should start with loading state`() = runTest {
        viewModel = UserListViewModel(mockRepository)

        val state = viewModel.uiState.value
        assertTrue("Initial state should be Loading, but was: $state",
            state is UserListUiState.Loading)
    }

    @Test
    fun `should transition from loading to empty when no users`() = runTest {
        viewModel = UserListViewModel(mockRepository)

        assertTrue("Should start with Loading",
            viewModel.uiState.value is UserListUiState.Loading)

        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertTrue("State should be Empty after loading",
            finalState is UserListUiState.Empty)
    }

    @Test
    fun `should load users successfully and update state to Success`() = runTest {
        viewModel = UserListViewModel(mockRepository)
        val users = listOf(createTestUser("1"), createTestUser("2"))

        assertTrue("Should start with Loading",
            viewModel.uiState.value is UserListUiState.Loading)

        usersFlow.emit(users)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is UserListUiState.Success)
        assertEquals(users, (state as UserListUiState.Success).users)
    }

    @Test
    fun `should show empty state when no users`() = runTest {
        viewModel = UserListViewModel(mockRepository)

        assertTrue("Should start with Loading",
            viewModel.uiState.value is UserListUiState.Loading)

        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Empty", state is UserListUiState.Empty)
    }

    @Test
    fun `should delete user when deleteUser is called`() = runTest {
        viewModel = UserListViewModel(mockRepository)
        val testUser = createTestUser()
        coEvery { mockRepository.deleteUser(testUser) } returns Unit

        viewModel.deleteUser(testUser)
        advanceUntilIdle()

        coVerify { mockRepository.deleteUser(testUser) }
    }

    @Test
    fun `should handle state transitions from empty to success`() = runTest {
        viewModel = UserListViewModel(mockRepository)

        usersFlow.emit(emptyList())
        advanceUntilIdle()
        val initialState = viewModel.uiState.value
        assertTrue("Should be Empty", initialState is UserListUiState.Empty)

        val users = listOf(createTestUser("1"))
        usersFlow.emit(users)
        advanceUntilIdle()

        val successState = viewModel.uiState.value
        assertTrue("Should transition to Success", successState is UserListUiState.Success)
        assertEquals(users, (successState as UserListUiState.Success).users)
    }

    @Test
    fun `should handle state transitions from success to empty`() = runTest {
        viewModel = UserListViewModel(mockRepository)

        val users = listOf(createTestUser("1"))
        usersFlow.emit(users)
        advanceUntilIdle()
        val initialState = viewModel.uiState.value
        assertTrue("Should start with Success", initialState is UserListUiState.Success)

        usersFlow.emit(emptyList())
        advanceUntilIdle()

        val emptyState = viewModel.uiState.value
        assertTrue("Should transition to Empty", emptyState is UserListUiState.Empty)
    }

    @Test
    fun `should maintain loading state until data is emitted`() = runTest {
        val delayedFlow = flow<List<User>> {
        }
        every { mockRepository.getUsers() } returns delayedFlow

        viewModel = UserListViewModel(mockRepository)

        val initialState = viewModel.uiState.value
        assertTrue("Should be Loading when no data emitted",
            initialState is UserListUiState.Loading)

        advanceUntilIdle()
        val stateAfterIdle = viewModel.uiState.value
        assertTrue("Should still be Loading after advanceUntilIdle",
            stateAfterIdle is UserListUiState.Loading)
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