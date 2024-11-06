package com.example.crm_service.entities.customer

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.contact.Contact
import com.example.crm_service.entities.jobOffer.JobOffer
import jakarta.persistence.*

@Entity
class Customer (
    var name: String,
    var surname: String
) : BaseEntity() {

    @OneToOne
    @MapsId
    var contact: Contact? = null

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var notes: MutableSet<Note> = mutableSetOf()

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var jobOffers: MutableSet<JobOffer> = mutableSetOf()

    fun addJobOffer(jobOffer: JobOffer) {
        jobOffers.add(jobOffer)
        jobOffer.customer = this
    }

    fun removeJobOffer(jobOffer: JobOffer) {
        jobOffers.remove(jobOffer)
        jobOffer.customer = null
    }

    fun addNotes(note: Note) {
        notes.add(note)
        note.customer = this
    }

    fun removeNotes(note: Note) {
        notes.remove(note)
        note.customer = null
    }
}