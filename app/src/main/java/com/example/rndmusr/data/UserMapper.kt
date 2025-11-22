package com.example.rndmusr.data

import com.example.rndmusr.data.local.entity.UserEntity
import com.example.rndmusr.data.remote.model.ApiUser
import com.example.rndmusr.domain.model.User

class UserMapper {

    fun mapToEntity(apiUser: ApiUser): UserEntity {
        return UserEntity(
            id = apiUser.login.uuid,
            gender = apiUser.gender,
            title = apiUser.name.title,
            firstName = apiUser.name.first,
            lastName = apiUser.name.last,
            email = apiUser.email,
            phone = apiUser.phone,
            cell = apiUser.cell,
            picture = apiUser.picture.large,
            nationality = apiUser.nationality,
            street = "${apiUser.location.street.number} ${apiUser.location.street.name}",
            city = apiUser.location.city,
            state = apiUser.location.state,
            country = apiUser.location.country,
            postcode = apiUser.location.postcode.toString(),
            latitude = apiUser.location.coordinates.latitude,
            longitude = apiUser.location.coordinates.longitude,
            timezoneOffset = apiUser.location.timezone.offset,
            timezoneDescription = apiUser.location.timezone.description,
            age = apiUser.dob.age,
            birthDate = apiUser.dob.date,
            createdAt = System.currentTimeMillis()
        )
    }

    fun mapToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            gender = entity.gender,
            title = entity.title,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phone = entity.phone,
            cell = entity.cell,
            picture = entity.picture,
            nationality = entity.nationality,
            street = entity.street,
            city = entity.city,
            state = entity.state,
            country = entity.country,
            postcode = entity.postcode,
            latitude = entity.latitude,
            longitude = entity.longitude,
            timezoneOffset = entity.timezoneOffset,
            timezoneDescription = entity.timezoneDescription,
            age = entity.age,
            birthDate = entity.birthDate,
            createdAt = entity.createdAt
        )
    }

    fun mapToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            gender = domain.gender,
            title = domain.title,
            firstName = domain.firstName,
            lastName = domain.lastName,
            email = domain.email,
            phone = domain.phone,
            cell = domain.cell,
            picture = domain.picture,
            nationality = domain.nationality,
            street = domain.street,
            city = domain.city,
            state = domain.state,
            country = domain.country,
            postcode = domain.postcode,
            latitude = domain.latitude,
            longitude = domain.longitude,
            timezoneOffset = domain.timezoneOffset,
            timezoneDescription = domain.timezoneDescription,
            age = domain.age,
            birthDate = domain.birthDate,
            createdAt = domain.createdAt
        )
    }
}