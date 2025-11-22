package com.example.rndmusr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val gender: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val cell: String,
    val picture: String,
    val nationality: String,
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String,
    val latitude: String,
    val longitude: String,
    val timezoneOffset: String,
    val timezoneDescription: String,
    val age: Int,
    val birthDate: String,
    val createdAt: Long
)
