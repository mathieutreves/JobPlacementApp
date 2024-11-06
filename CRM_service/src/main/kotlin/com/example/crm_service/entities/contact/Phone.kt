package com.example.crm_service.entities.contact

import com.example.crm_service.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "phone")
class Phone (
    var number: String
) : BaseEntity() {

    @ManyToMany(mappedBy = "phoneNumbers")
    val contacts: MutableSet<Contact> = mutableSetOf()
}