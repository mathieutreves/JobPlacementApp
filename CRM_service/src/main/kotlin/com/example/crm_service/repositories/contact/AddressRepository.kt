package com.example.crm_service.repositories.contact

import com.example.crm_service.entities.contact.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository: JpaRepository<Address, Long> {
    fun findByStreetAndNumberAndCityAndPostalCodeAndCountry(street: String, number:String, city: String,  postalCode: String, country: String): Address?
}