package com.example.crm_service.entities.contact

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.entities.customer.Customer
import com.example.crm_service.entities.professional.Professional
import jakarta.persistence.*

@Entity
@Table(name = "contact")
class Contact (
    var name: String,
    var surname: String,
    var ssnCode: String?,
    var category: String,
) : BaseEntity() {

    @OneToOne(mappedBy = "contact", cascade = [(CascadeType.ALL)])
    var professionalProfile: Professional? = null

    @OneToOne(mappedBy = "contact", cascade = [(CascadeType.ALL)])
    var customerProfile: Customer? = null

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(name = "contact_email",
        joinColumns = [(JoinColumn(name = "contact_id", referencedColumnName = "id"))],
        inverseJoinColumns = [(JoinColumn(name = "email_id", referencedColumnName = "id"))])
    val emails: MutableSet<Email> = mutableSetOf()

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(name = "contact_address",
        joinColumns = [(JoinColumn(name = "contact_id", referencedColumnName = "id"))],
        inverseJoinColumns = [(JoinColumn(name = "address_id", referencedColumnName = "id"))])
    val addresses: MutableSet<Address> = mutableSetOf()

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(name = "contact_phone",
        joinColumns = [(JoinColumn(name = "contact_id", referencedColumnName = "id"))],
        inverseJoinColumns = [(JoinColumn(name = "phone_id", referencedColumnName = "id"))])
    val phoneNumbers: MutableSet<Phone> = mutableSetOf()

    fun addEmail(email: Email) {
        emails.add(email)
        email.contacts.add(this)
    }

    fun removeEmail(email: Email) {
        emails.remove(email)
        email.contacts.remove(this)
    }

    fun addAddress(address: Address) {
        addresses.add(address)
        address.contacts.add(this)
    }

    fun removeAddress(address: Address) {
        addresses.remove(address)
        address.contacts.remove(this)
    }

    fun addTelephone(phone: Phone) {
        phoneNumbers.add(phone)
        phone.contacts.add(this)
    }

    fun removeTelephone(phone: Phone) {
        phoneNumbers.remove(phone)
        phone.contacts.remove(this)
    }
}