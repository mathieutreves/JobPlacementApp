package com.example.crm_service.dtos.contact

import com.example.crm_service.entities.contact.Phone

data class PhoneDTO(
    val id: Long,
    val number: String
)

fun Phone.toDTO(): PhoneDTO =
    PhoneDTO(this.id, this.number)