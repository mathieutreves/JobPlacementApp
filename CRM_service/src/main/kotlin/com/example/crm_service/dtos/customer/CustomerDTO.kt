package com.example.crm_service.dtos.customer

import com.example.crm_service.dtos.contact.SimpleContactDTO
import com.example.crm_service.dtos.contact.toSimpleDTO
import com.example.crm_service.dtos.jobOffer.JobOfferDTO
import com.example.crm_service.dtos.jobOffer.toDTO
import com.example.crm_service.entities.customer.Customer

data class CustomerDTO(
    val id: Long,
    val contactInfo: SimpleContactDTO?,
    val notes: List<String>,
    val jobOffers: List<JobOfferDTO>
)

data class SimpleCustomerDTO(
    val id: Long,
    val notes: List<String>,
    val jobOffers: List<JobOfferDTO>
)

fun Customer.toDTO() = CustomerDTO(this.id, this.contact?.toSimpleDTO(), this.notes.map { it.note},
    this.jobOffers.map { it.toDTO()})

fun Customer.toSimpleDTO() = SimpleCustomerDTO(this.id, this.notes.map { it.note},
    this.jobOffers.map { it.toDTO()})