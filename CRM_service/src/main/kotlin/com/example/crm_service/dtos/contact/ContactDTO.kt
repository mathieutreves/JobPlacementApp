package com.example.crm_service.dtos.contact

import com.example.crm_service.dtos.customer.SimpleCustomerDTO
import com.example.crm_service.dtos.customer.toSimpleDTO
import com.example.crm_service.dtos.professional.SimpleProfessionalDTO
import com.example.crm_service.dtos.professional.toSimpleDTO
import com.example.crm_service.entities.contact.Contact

data class ContactDTO(
    val id: Long,
    val name: String,
    val surname: String,
    val ssnCode: String?,
    val category: String,
    val emails: List<EmailDTO>,
    val addresses: List<AddressDTO>,
    val telephoneNumbers: List<PhoneDTO>,
    val professionalProfile: SimpleProfessionalDTO?,
    val customerProfile: SimpleCustomerDTO?
)


data class SimpleContactDTO(
    val id: Long,
    val name: String,
    val surname: String,
    val ssnCode: String?,
    val category: String,
    val emails: List<EmailDTO>,
    val addresses: List<AddressDTO>,
    val telephoneNumbers: List<PhoneDTO>,
)

fun Contact.toDTO(): ContactDTO =
    ContactDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        ssnCode = this.ssnCode,
        category = this.category,
        emails = this.emails.map { it.toDTO() },
        addresses = this.addresses.map { it.toDTO() },
        telephoneNumbers = this.phoneNumbers.map { it.toDTO() },
        professionalProfile = this.professionalProfile?.toSimpleDTO(),
        customerProfile = this.customerProfile?.toSimpleDTO()
    )

fun Contact.toSimpleDTO(): SimpleContactDTO =
    SimpleContactDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        ssnCode = this.ssnCode,
        category = this.category,
        emails = this.emails.map { it.toDTO() },
        addresses = this.addresses.map { it.toDTO() },
        telephoneNumbers = this.phoneNumbers.map { it.toDTO() }
    )
