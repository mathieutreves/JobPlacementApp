package com.example.crm_service.entities.contact

import com.example.crm_service.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "address")
class Address (
    var street: String,
    var number: String,
    var city: String,
    var postalCode: String,
    var country: String
) : BaseEntity() {

    @ManyToMany(mappedBy = "addresses")
    val contacts: MutableSet<Contact> = mutableSetOf()
}