package com.example.crm_service.repositories.professional

import com.example.crm_service.entities.professional.Professional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProfessionalRepository: JpaRepository<Professional, Long>, JpaSpecificationExecutor<Professional> {
    fun findByNameAndSurname(name: String, surname: String): Professional?
}