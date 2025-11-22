package com.example.rndmusr.domain.model

data class User(
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
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$title $firstName $lastName"

    val fullAddress: String
        get() = "$street, $city, $state, $country"
}