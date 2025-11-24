package com.example.rndmusr.domain.model

import org.junit.Assert
import org.junit.Test

class UserTest {

    @Test
    fun `should create user with correct full name`() {
        val user = User(
            id = "1",
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
            birthDate = "1993-05-15"
        )

        // When & Then
        Assert.assertEquals("Mr John Doe", user.fullName)
        Assert.assertEquals("123 Main St, New York, NY, USA", user.fullAddress)
    }
}