package com.example.rndmusr.utils

import com.example.rndmusr.data.local.entity.UserEntity
import com.example.rndmusr.data.remote.model.ApiUser
import com.example.rndmusr.data.remote.model.Coordinates
import com.example.rndmusr.data.remote.model.Dob
import com.example.rndmusr.data.remote.model.Location
import com.example.rndmusr.data.remote.model.Login
import com.example.rndmusr.data.remote.model.Name
import com.example.rndmusr.data.remote.model.Picture
import com.example.rndmusr.data.remote.model.Street
import com.example.rndmusr.data.remote.model.Timezone
import com.example.rndmusr.domain.model.User

object TestDataFactory {

    fun createTestUser(
        id: String = "1",
        gender: String = "male",
        title: String = "Mr",
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
        phone: String = "+1234567890",
        nationality: String = "US"
    ): User {
        return User(
            id = id,
            gender = gender,
            title = title,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            cell = "+1234567890",
            picture = "https://example.com/photo.jpg",
            nationality = nationality,
            street = "123 Main St",
            city = "New York",
            state = "NY",
            country = "USA",
            postcode = "10001",
            latitude = "40.7128",
            longitude = "-74.0060",
            timezoneOffset = "-5:00",
            timezoneDescription = "Eastern Standard Time",
            age = 30,
            birthDate = "1993-05-15T00:00:00.000Z"
        )
    }

    fun createTestApiUser(): ApiUser {
        return ApiUser(
            gender = "male",
            name = Name(
                title = "Mr",
                first = "John",
                last = "Doe"
            ),
            location = Location(
                street = Street(
                    number = 123,
                    name = "Main St"
                ),
                city = "New York",
                state = "NY",
                country = "USA",
                postcode = "10001",
                coordinates = Coordinates(
                    latitude = "40.7128",
                    longitude = "-74.0060"
                ),
                timezone = Timezone(
                    offset = "-5:00",
                    description = "Eastern Standard Time"
                )
            ),
            email = "john.doe@example.com",
            login = Login(uuid = "test-uuid-123"),
            phone = "+1234567890",
            cell = "+0987654321",
            picture = Picture(
                large = "https://example.com/large.jpg",
                medium = "https://example.com/medium.jpg",
                thumbnail = "https://example.com/thumbnail.jpg"
            ),
            nationality = "US",
            dob = Dob(
                date = "1993-05-15T00:00:00.000Z",
                age = 30
            )
        )
    }

    fun createTestUserEntity(): UserEntity {
        return UserEntity(
            id = "test-uuid-123",
            gender = "male",
            title = "Mr",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "+1234567890",
            cell = "+0987654321",
            picture = "https://example.com/large.jpg",
            nationality = "US",
            street = "123 Main St",
            city = "New York",
            state = "NY",
            country = "USA",
            postcode = "10001",
            latitude = "40.7128",
            longitude = "-74.0060",
            timezoneOffset = "-5:00",
            timezoneDescription = "Eastern Standard Time",
            age = 30,
            birthDate = "1993-05-15T00:00:00.000Z",
            createdAt = System.currentTimeMillis()
        )
    }
}