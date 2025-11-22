package com.example.rndmusr.domain.repository

import com.example.rndmusr.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getRandomUser(gender: String?, nationality: String?): Result<User>
    fun getUsers(): Flow<List<User>>
    suspend fun getUserById(id: String): User?
    suspend fun saveUser(user: User)
    suspend fun deleteUser(user: User)
}