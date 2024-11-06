package com.example.crm_service.entities.jobOffer

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.customer.Customer
import com.example.crm_service.entities.professional.Professional
import com.example.crm_service.services.jobOffer.JobOfferState
import com.example.crm_service.services.professional.EmploymentState
import jakarta.persistence.*

@Entity
class JobOffer (
    var description: String,
    var status: JobOfferState,
    var duration: String,
    var value: String?,
    var profitMargin: Int = 1
): BaseEntity() {
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var skills: MutableSet<Skill> = mutableSetOf()

    @OneToMany(mappedBy = "jobOffer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var notes: MutableSet<Note> = mutableSetOf()

    @ManyToOne
    var customer: Customer? = null

    @ManyToOne
    var professional: Professional? = null

    fun addNotes(note: Note) {
        notes.add(note)
        note.jobOffer = this
    }

    fun removeNotes(note: Note) {
        notes.remove(note)
        note.jobOffer = null
    }

    fun addSkill(skill: Skill) {
        skills.add(skill)
        skill.jobOffers.add(this)
    }

    fun removeSkill(skill: Skill) {
        skills.remove(skill)
        skill.jobOffers.remove(this)
    }

    fun addProfessional(professional: Professional) {
        this.professional = professional
        this.value = (professional.dailyRate.toInt() * duration.toInt() * profitMargin).toString()
        professional.jobOffers.add(this)
    }

    fun removeProfessional(professional: Professional) {
        this.professional = null
        this.value = null
        professional.jobOffers.remove(this)
    }
}