package com.example.crm_service.repositories.contact

import com.example.crm_service.entities.contact.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository: JpaRepository<Email, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Email?
}