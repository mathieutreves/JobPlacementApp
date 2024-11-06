package com.example.crm_service.entities

import com.example.crm_service.entities.customer.Customer
import com.example.crm_service.entities.jobOffer.JobOffer
import com.example.crm_service.entities.professional.Professional
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class Note (
    var note: String
): BaseEntity() {
    @ManyToOne
    var customer: Customer? = null

    @ManyToOne
    var professional: Professional? = null

    @ManyToOne
    var jobOffer: JobOffer? = null
}