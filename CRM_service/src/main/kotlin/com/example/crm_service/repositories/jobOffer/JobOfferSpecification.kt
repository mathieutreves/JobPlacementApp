package com.example.crm_service.repositories.jobOffer

import com.example.crm_service.entities.jobOffer.JobOffer
import com.example.crm_service.entities.professional.Professional
import com.example.crm_service.entities.customer.Customer
import com.example.crm_service.services.jobOffer.JobOfferState
import org.springframework.data.jpa.domain.Specification

class JobOfferSpecification {
    companion object {
        fun withStatus(jobOfferState: JobOfferState?): Specification<JobOffer>? {
            return jobOfferState?.let {
                Specification<JobOffer> { root, _, cb ->
                    cb.like(root.get("jobOfferState"), jobOfferState.name)
                }
            }
        }
        fun withProfessional(professionalId: Long?): Specification<JobOffer>? {
            return professionalId?.let {
                Specification<JobOffer> { root, _, cb ->
                    cb.equal(root.get<Professional>("professional").get<Long>("id"), professionalId)
                }
            }
        }

        fun withCustomer(customerId: Long?): Specification<JobOffer>? {
            return customerId?.let {
                Specification<JobOffer> { root, _, cb ->
                    cb.equal(root.get<Customer>("customer").get<Long>("id"), customerId)
                }
            }
        }
    }
}