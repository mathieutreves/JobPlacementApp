package com.example.crm_service.repositories.contact

import com.example.crm_service.entities.contact.Phone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneRepository: JpaRepository<Phone, Long> {
    fun existsByNumber(phone: String): Boolean
    fun findByNumber(phone: String): Phone?
}