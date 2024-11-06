package com.example.crm_service.repositories.contact

import com.example.crm_service.entities.contact.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository: JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {
    fun findByNameAndSurname(name: String, surname: String): Contact?
}