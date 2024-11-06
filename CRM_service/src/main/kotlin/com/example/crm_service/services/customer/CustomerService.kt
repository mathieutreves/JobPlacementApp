package com.example.crm_service.services.customer

import com.example.crm_service.dtos.customer.CustomerDTO
import org.springframework.http.ResponseEntity

interface CustomerService {
    fun getAllCustomer(page: Int, size: Int): ResponseEntity<List<CustomerDTO>>
    fun createCustomer(name: String, surname: String): ResponseEntity<String>
    fun addNoteToCustomer(customerId: Long, note: String): ResponseEntity<String>
    fun removeNoteFromCustomer(customerId: Long, noteId: Long): ResponseEntity<String>
    fun addJobOfferToCustomer(customerId: Long, jobOfferId: Long): ResponseEntity<String>
    fun removeJobOfferFromCustomer(customerId: Long, jobOfferId: Long): ResponseEntity<String>
}