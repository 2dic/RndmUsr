package com.example.rndmusr.data

import org.junit.Test
import org.junit.Assert.*
import com.example.rndmusr.utils.TestDataFactory

class UserMapperTest {

    private val mapper = UserMapper()

    @Test
    fun `should map api user to entity correctly`() {
        val apiUser = TestDataFactory.createTestApiUser()

        val entity = mapper.mapToEntity(apiUser)

        assertEquals(apiUser.login.uuid, entity.id)
        assertEquals(apiUser.gender, entity.gender)
        assertEquals(apiUser.name.title, entity.title)
        assertEquals(apiUser.name.first, entity.firstName)
        assertEquals(apiUser.name.last, entity.lastName)
        assertEquals(apiUser.email, entity.email)
        assertEquals("${apiUser.location.street.number} ${apiUser.location.street.name}", entity.street)
        assertEquals(apiUser.location.city, entity.city)
        assertEquals(apiUser.location.state, entity.state)
        assertEquals(apiUser.location.country, entity.country)
        assertEquals(apiUser.dob.age, entity.age)
        assertEquals(apiUser.dob.date, entity.birthDate)
    }

    @Test
    fun `should map entity to domain correctly`() {
        val entity = TestDataFactory.createTestUserEntity()

        val domain = mapper.mapToDomain(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.gender, domain.gender)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.firstName, domain.firstName)
        assertEquals(entity.lastName, domain.lastName)
        assertEquals("${entity.title} ${entity.firstName} ${entity.lastName}", domain.fullName)
        assertEquals(entity.email, domain.email)
        assertEquals(entity.phone, domain.phone)
        assertEquals(entity.picture, domain.picture)
    }

    @Test
    fun `should map domain to entity correctly`() {
        val domain = TestDataFactory.createTestUser()

        val entity = mapper.mapToEntity(domain)

        assertEquals(domain.id, entity.id)
        assertEquals(domain.gender, entity.gender)
        assertEquals(domain.title, entity.title)
        assertEquals(domain.firstName, entity.firstName)
        assertEquals(domain.lastName, entity.lastName)
        assertEquals(domain.email, entity.email)
        assertEquals(domain.phone, entity.phone)
        assertEquals(domain.picture, entity.picture)
    }
}