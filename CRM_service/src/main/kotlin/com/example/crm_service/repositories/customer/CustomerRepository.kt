package com.example.crm_service.repositories.customer

import com.example.crm_service.entities.customer.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long> {
    fun findByName(name: String): Customer?
}