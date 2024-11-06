package com.example.crm_service.entities.jobOffer

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.entities.jobOffer.JobOffer
import com.example.crm_service.entities.professional.Professional
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany

@Entity
class Skill(
    var name: String
): BaseEntity() {
    @ManyToMany(mappedBy = "skills")
    var jobOffers = mutableSetOf<JobOffer>()

    @ManyToMany(mappedBy = "skills")
    var professionals = mutableSetOf<Professional>()
}