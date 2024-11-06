package com.example.crm_service.services.jobOffer

import com.example.crm_service.dtos.jobOffer.JobOfferDTO
import org.springframework.http.ResponseEntity

interface JobOfferService {
    fun createJobOffer(description: String, duration: String, customerId: Long): ResponseEntity<String>
    fun getAllJobOffers(page: Int, size: Int): ResponseEntity<List<JobOfferDTO>>
    fun addSkillToJobOffer(jobOffersId: Long, newSkill: String): ResponseEntity<String>
    fun removeSkillFromJobOffers(jobOffersId: Long, skillId: Long): ResponseEntity<String>
    fun addProfessionalToJobOffer(jobOffersId: Long, professionalId: Long): ResponseEntity<String>
    fun removeProfessionalFromJobOffer(jobOffersId: Long, professionalId: Long): ResponseEntity<String>
    fun getAllOpenJobOffersOfCustomer(customerId: Long, page: Int, size: Int): ResponseEntity<List<JobOfferDTO>>
    fun getAllAcceptedJobOffersOfProfessional(professionalId: Long, page: Int, size: Int): ResponseEntity<List<JobOfferDTO>>
    fun getAllRegistered(page: Int, size: Int, customerId: Long?, professionalId: Long?): ResponseEntity<List<JobOfferDTO>>
    fun changeJobOfferStatus(jobOffersId: Long, status: String, note: String?, professionalId: Long?): ResponseEntity<String>
    fun getState(jobOffersId: Long): ResponseEntity<String>
    fun getJobOfferValue(jobOffersId: Long): ResponseEntity<String>
}