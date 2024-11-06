package com.example.crm_service.dtos.contact

import com.example.crm_service.entities.contact.Address

data class AddressDTO(
    val id: Long,
    val street: String,
    val number: String,
    val city: String,
    val postalCode: String,
    val country: String
)

fun Address.toDTO(): AddressDTO =
    AddressDTO(this.id, this.street, this.number, this.city, this.postalCode, this.country)