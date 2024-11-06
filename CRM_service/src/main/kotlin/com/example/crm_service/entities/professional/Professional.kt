package com.example.crm_service.entities.professional

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.contact.Contact
import com.example.crm_service.entities.jobOffer.JobOffer
import com.example.crm_service.entities.jobOffer.Skill
import com.example.crm_service.services.professional.EmploymentState
import jakarta.persistence.*

@Entity
class Professional (
    var name: String,
    var surname: String,
    var location: String,
    var dailyRate: String,

    @Enumerated(EnumType.STRING)
    var employmentState: EmploymentState
): BaseEntity() {

    @OneToOne
    @MapsId
    var contact: Contact? = null

    @ManyToMany
    var skills: MutableSet<Skill> = mutableSetOf()

    @OneToMany(mappedBy = "professional", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var notes: MutableSet<Note> = mutableSetOf()

    @OneToMany(mappedBy = "professional", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var jobOffers: MutableSet<JobOffer> = mutableSetOf()

    fun addSkill(skill: Skill) {
        skills.add(skill)
        skill.professionals.add(this)
    }

    fun removeSkill(skill: Skill) {
        skills.remove(skill)
        skill.professionals.remove(this)
    }

    fun addNote(note: Note){
        notes.add(note)
        note.professional = this
    }

    fun removeNote(note: Note){
        notes.remove(note)
        note.professional = null
    }

    fun changeEmploymentState(state: EmploymentState) {
        this.employmentState = state
    }
}