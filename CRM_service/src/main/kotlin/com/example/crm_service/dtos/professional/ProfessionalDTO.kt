package com.example.crm_service.dtos.professional

import com.example.crm_service.dtos.contact.SimpleContactDTO
import com.example.crm_service.dtos.contact.toSimpleDTO
import com.example.crm_service.dtos.jobOffer.JobOfferDTO
import com.example.crm_service.dtos.jobOffer.toDTO
import com.example.crm_service.entities.professional.Professional
import com.example.crm_service.services.professional.EmploymentState

data class ProfessionalDTO(
    val id: Long,
    val contactInfo: SimpleContactDTO?,
    val location: String,
    val employmentState: EmploymentState,
    val skills: List<String>,
    val notes: List<String>,
    val jobOffers: List<JobOfferDTO>
)

data class SimpleProfessionalDTO(
    val id: Long,
    val location: String,
    val employmentState: EmploymentState,
    val skills: List<String>,
    val notes: List<String>,
    val jobOffers: List<JobOfferDTO>
)

fun Professional.toDTO(): ProfessionalDTO =
    ProfessionalDTO(this.id, this.contact?.toSimpleDTO(), this.location, this.employmentState, this.skills.map { it.name },
        this.notes.map { it.note}, this.jobOffers.map { it.toDTO() }
    )

fun Professional.toSimpleDTO(): SimpleProfessionalDTO =
    SimpleProfessionalDTO(this.id, this.location, this.employmentState, this.skills.map { it.name },
        this.notes.map { it.note}, this.jobOffers.map { it.toDTO() }
    )