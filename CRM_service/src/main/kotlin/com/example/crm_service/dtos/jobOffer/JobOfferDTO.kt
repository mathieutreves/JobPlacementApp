package com.example.crm_service.dtos.jobOffer

import com.example.crm_service.entities.jobOffer.JobOffer

data class JobOfferDTO(
    val id: Long,
    val description: String,
    val status: String,
    val duration: String,
    var value: String?,
    var profitMargin: Int = 1,
    var skills: List<String>,
    var notes: List<String>,
    var customerId: Long?,
    var professionalId: Long?
)

fun JobOffer.toDTO() = JobOfferDTO(this.id, this.description, this.status.toString(), this.duration, this.value,
    this.profitMargin, this.skills.map { it.name }, this.notes.map { it.note }, this.customer?.id, this.professional?.id)