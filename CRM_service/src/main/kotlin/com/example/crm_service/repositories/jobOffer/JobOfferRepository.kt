package com.example.crm_service.repositories.jobOffer

import com.example.crm_service.entities.jobOffer.JobOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface JobOfferRepository: JpaRepository<JobOffer, Long>, JpaSpecificationExecutor<JobOffer> {
}