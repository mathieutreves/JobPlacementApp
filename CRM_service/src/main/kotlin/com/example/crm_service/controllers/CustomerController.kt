package com.example.crm_service.controllers

import com.example.crm_service.dtos.customer.CustomerDTO
import com.example.crm_service.services.customer.CustomerService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/customers")
class CustomerController(private val customerService: CustomerService) {

    @GetMapping("","/")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getCustomers(
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ): ResponseEntity<List<CustomerDTO>> {
        return customerService.getAllCustomer(page, size)
    }

    @PostMapping("","/")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun createCustomer(
        @RequestParam("name") name: String,
        @RequestParam("surname") surname: String
    ): ResponseEntity<String> {
        return customerService.createCustomer(name, surname)
    }

    @PostMapping("/{customerId}/notes")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addNoteToCustomer(
        @PathVariable("customerId") customerId: Long,
        @RequestParam("note") note: String
    ): ResponseEntity<String> {
        return customerService.addNoteToCustomer(customerId, note)
    }

    @DeleteMapping("/{customerId}/notes/{noteId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun removeNoteFromCustomer(
        @PathVariable("customerId") customerId: Long,
        @PathVariable("noteId") noteId: Long
    ): ResponseEntity<String> {
        return customerService.removeNoteFromCustomer(customerId, noteId)
    }

    @PostMapping("/{customerId}/jobOffers")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addJobOfferToCustomer(
        @PathVariable("customerId") customerId: Long,
        @RequestParam("jobOfferId") jobOfferId: Long
    ): ResponseEntity<String> {
        return customerService.addJobOfferToCustomer(customerId, jobOfferId)
    }

    @DeleteMapping("/{customerId}/jobOffers/{jobOfferId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun removeJobOfferFromCustomer(
        @PathVariable("customerId") customerId: Long,
        @PathVariable("jobOfferId") jobOfferId: Long
    ): ResponseEntity<String> {
        return customerService.removeJobOfferFromCustomer(customerId, jobOfferId)
    }
}