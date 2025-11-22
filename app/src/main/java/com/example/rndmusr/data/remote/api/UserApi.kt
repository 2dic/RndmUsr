package com.example.rndmusr.data.remote.api

import com.example.rndmusr.data.remote.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("api/")
    suspend fun getRandomUser(
        @Query("gender") gender: String? = null,
        @Query("nat") nationality: String? = null
    ): ApiResponse
}