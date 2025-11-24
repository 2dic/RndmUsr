package com.example.rndmusr.data.repository

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import com.example.rndmusr.data.remote.api.UserApi
import com.example.rndmusr.data.local.dao.UserDao
import com.example.rndmusr.data.UserMapper
import com.example.rndmusr.utils.TestDataFactory
import com.example.rndmusr.data.remote.model.ApiResponse

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private val userApi: UserApi = mockk()
    private val userDao: UserDao = mockk()
    private val mapper: UserMapper = mockk()

    @Before
    fun setUp() {
        repository = UserRepositoryImpl(userApi, userDao, mapper)
    }

    @Test
    fun `getRandomUser should return success when api call succeeds`() = runTest {
        // Given
        val apiUser = TestDataFactory.createTestApiUser()
        val entity = TestDataFactory.createTestUserEntity()
        val domain = TestDataFactory.createTestUser()

        coEvery { userApi.getRandomUser(any(), any()) } returns ApiResponse(
            results = listOf(apiUser)
        )
        coEvery { mapper.mapToEntity(apiUser) } returns entity
        coEvery { mapper.mapToDomain(entity) } returns domain

        // When
        val result = repository.getRandomUser("male", "US")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(domain, result.getOrNull())

        // Verify mapping was called
        coVerify { mapper.mapToEntity(apiUser) }
        coVerify { mapper.mapToDomain(entity) }
    }

    @Test
    fun `getRandomUser should return failure when api returns empty list`() = runTest {
        // Given
        coEvery { userApi.getRandomUser(any(), any()) } returns ApiResponse(
            results = emptyList()
        )

        // When
        val result = repository.getRandomUser("male", "US")

        // Then
        assertTrue(result.isFailure)
        assertEquals("No user data received", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRandomUser should return failure when api call fails`() = runTest {
        // Given
        coEvery { userApi.getRandomUser(any(), any()) } throws Exception("Network error")

        // When
        val result = repository.getRandomUser("male", "US")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveUser should call dao insert`() = runTest {
        // Given
        val user = TestDataFactory.createTestUser()
        val entity = TestDataFactory.createTestUserEntity()

        coEvery { mapper.mapToEntity(user) } returns entity
        coEvery { userDao.insertUser(entity) } returns Unit

        // When
        repository.saveUser(user)

        // Then
        coVerify { userDao.insertUser(entity) }
    }

    @Test
    fun `getUsers should return flow from dao`() = runTest {
        // Given
        val entities = listOf(TestDataFactory.createTestUserEntity())
        val domains = listOf(TestDataFactory.createTestUser())

        coEvery { userDao.getUsers() } returns kotlinx.coroutines.flow.flowOf(entities)
        coEvery { mapper.mapToDomain(any()) } returns domains[0]

        // When
        val result = repository.getUsers()

        // Then
        result.collect { users ->
            assertEquals(domains, users)
        }
    }
}