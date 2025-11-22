package com.example.rndmusr.data.repository

import com.example.rndmusr.data.UserMapper
import com.example.rndmusr.data.local.dao.UserDao
import com.example.rndmusr.data.remote.api.UserApi
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDao,
    private val mapper: UserMapper
) : UserRepository {

    override suspend fun getRandomUser(gender: String?, nationality: String?): Result<User> {
        return try {
            val response = userApi.getRandomUser(gender, nationality)
            if (response.results.isNotEmpty()) {
                val apiUser = response.results[0]
                val userEntity = mapper.mapToEntity(apiUser)
                val user = mapper.mapToDomain(userEntity)
                Result.success(user)
            } else {
                Result.failure(Exception("No user data received"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUsers(): Flow<List<User>> {
        return userDao.getUsers().map { entities ->
            entities.map { mapper.mapToDomain(it) }
        }
    }

    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)?.let { mapper.mapToDomain(it) }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(mapper.mapToEntity(user))
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(mapper.mapToEntity(user))
    }
}