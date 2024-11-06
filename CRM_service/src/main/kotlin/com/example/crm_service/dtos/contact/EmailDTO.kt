package com.example.crm_service.dtos.contact

import com.example.crm_service.entities.contact.Email

data class EmailDTO(
    val id: Long,
    val email: String
)

fun Email.toDTO(): EmailDTO =
    EmailDTO(this.id, this.email)