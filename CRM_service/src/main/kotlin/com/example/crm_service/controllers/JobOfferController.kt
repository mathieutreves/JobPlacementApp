package com.example.crm_service.controllers

import com.example.crm_service.dtos.jobOffer.JobOfferDTO
import com.example.crm_service.services.jobOffer.JobOfferService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/API/jobOffers")
class JobOfferController (private val jobOfferService: JobOfferService) {

    @PostMapping("", "/")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun createJobOffer(
        @RequestParam("description") description: String,
        @RequestParam("duration") duration: String,
        @RequestParam("customerId") customerId: Long
    ) : ResponseEntity<String> {
        return jobOfferService.createJobOffer(description, duration, customerId)
    }

    @GetMapping("/", "")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAll(
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<JobOfferDTO>> {
        return jobOfferService.getAllJobOffers(page, size)
    }

    @PostMapping("/{jobOffersId}/skills")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addSkillToJobOffer(
        @PathVariable("jobOffersId") jobOffersId: Long,
        @RequestParam("skill") skill: String,
    ) : ResponseEntity<String> {
        return jobOfferService.addSkillToJobOffer(jobOffersId, skill)
    }

    @DeleteMapping("/{jobOffersId}/skills/{skillId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteSkillFromJobOffers(
        @PathVariable("jobOffersId") jobOffersId: Long,
        @PathVariable("skillId") skillId: Long,
    ) : ResponseEntity<String> {
        return jobOfferService.removeSkillFromJobOffers(jobOffersId, skillId)
    }

    @GetMapping("/open/{customerId}")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllOpenJobOffersOfCustomer(
        @PathVariable("customerId") customerId: Long,
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<JobOfferDTO>> {
        return jobOfferService.getAllOpenJobOffersOfCustomer(customerId, page, size)
    }

    @GetMapping("/accepted/{professionalId}")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllAcceptedJobOffersOfProfessional(
        @PathVariable("professionalId") professionalId: Long,
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<JobOfferDTO>> {
        return jobOfferService.getAllAcceptedJobOffersOfProfessional(professionalId, page, size)
    }

    @GetMapping("/aborted")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllRegistered(
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int,
        @RequestParam("customerId", required = false) customerId: Long?,
        @RequestParam("professionalId", required = false) professionalId: Long?
    ) : ResponseEntity<List<JobOfferDTO>> {
        return jobOfferService.getAllRegistered(page, size, customerId, professionalId)
    }

    @PostMapping("/{jobOffersId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeJobOfferStatus(
        @PathVariable("jobOffersId") jobOffersId: Long,
        @RequestParam("status") status: String,
        @RequestParam("note") note: String?,
        @RequestParam("professionalId", required = false) professionalId: Long?
    ) : ResponseEntity<String> {
        return jobOfferService.changeJobOfferStatus(jobOffersId, status, note, professionalId)
    }

    @GetMapping("/{jobOffersId}/state")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getState(
        @PathVariable("jobOffersId") jobOffersId: Long
    ) : ResponseEntity<String> {
        return jobOfferService.getState(jobOffersId)
    }

    @GetMapping("/{jobOffersId}/value")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getJobOfferValue(
        @PathVariable("jobOffersId") jobOffersId: Long
    ) : ResponseEntity<String> {
        return jobOfferService.getJobOfferValue(jobOffersId)
    }
}