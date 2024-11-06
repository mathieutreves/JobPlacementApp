package com.example.crm_service.entities.contact

import com.example.crm_service.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "email")
class Email (
    var email: String
) : BaseEntity() {

    @ManyToMany(mappedBy = "emails")
    val contacts: MutableSet<Contact> = mutableSetOf()
}