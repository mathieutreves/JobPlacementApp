package com.example.crm_service.services.jobOffer

import com.example.crm_service.dtos.jobOffer.JobOfferDTO
import com.example.crm_service.dtos.jobOffer.toDTO
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.jobOffer.JobOffer
import com.example.crm_service.entities.jobOffer.Skill
import com.example.crm_service.exceptions.customer.CustomerNotFoundException
import com.example.crm_service.exceptions.jobOffer.JobOfferNotFoundException
import com.example.crm_service.exceptions.jobOffer.JobOfferStateInvalidTransitionException
import com.example.crm_service.exceptions.professional.ProfessionalNotFoundException
import com.example.crm_service.exceptions.professional.SkillNotFoundException
import com.example.crm_service.repositories.customer.CustomerRepository
import com.example.crm_service.repositories.jobOffer.*
import com.example.crm_service.repositories.professional.ProfessionalRepository
import com.example.crm_service.services.contact.ContactServiceImpl
import com.example.crm_service.services.professional.EmploymentState
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class JobOfferServiceImpl(
    private val jobOfferRepository: JobOfferRepository,
    private val skillRepo: SkillRepository,
    private val professionalRepository: ProfessionalRepository,
    private val customerRepository: CustomerRepository
): JobOfferService {

    private val logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)

    override fun createJobOffer(description: String, duration: String, customerId: Long): ResponseEntity<String> {
        val customer = customerRepository.findByIdOrNull(customerId)
            ?: throw CustomerNotFoundException("Customer not found: $customerId")

        val newJobOffer = JobOffer(
            description = description,
            status = JobOfferState.CREATED,
            duration = duration,
            value = null,
        )

        // Save jobOffer and add it to the customer
        jobOfferRepository.save(newJobOffer)
        customer.addJobOffer(newJobOffer)

        logger.info("JobOffer created successfully with id ${newJobOffer.id}")
        return ResponseEntity("JobOffer created successfully", HttpStatus.CREATED)
    }

    override fun getAllJobOffers(page: Int, size: Int): ResponseEntity<List<JobOfferDTO>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity(jobOfferRepository.findAll(pageable).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun addSkillToJobOffer(jobOffersId: Long, newSkill: String): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        var responseStatus = HttpStatus.OK
        val skill = skillRepo.findByName(newSkill) ?: Skill(newSkill).also { skillRepo.save(it) }
            .also { logger.info("Created new skill with id ${it.id}") }.also { responseStatus = HttpStatus.CREATED }
        jobOffer.addSkill(skill)
        logger.info("Skill $newSkill added successfully to JobOffer $jobOffersId with id: ${skill.id}")
        return ResponseEntity("Skill added successfully", responseStatus)
    }

    override fun removeSkillFromJobOffers(jobOffersId: Long, skillId: Long): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        val skillToRemove = skillRepo.findByIdOrNull(skillId)
            ?: throw SkillNotFoundException("Skill not found: $skillId")

        // Check if the skill is actually associated to this contact
        return if (jobOffer.skills.contains(skillToRemove)) {
            // Remove skill from contact and vice versa
            jobOffer.removeSkill(skillToRemove)

            // Check if the skill has other contact associated to it. If not, remove it from the DB.
            if(skillToRemove.professionals.isEmpty() && skillToRemove.jobOffers.isEmpty()){
                skillRepo.deleteById(skillId)
                logger.info("Email with id $skillId removed from the DB")
            }

            logger.info("Skill $skillToRemove removed successfully from JobOffer $jobOffersId")
            ResponseEntity("Skill removed successfully from JobOffer", HttpStatus.OK)
        } else {
            ResponseEntity("This skill is not associated to the JobOffer", HttpStatus.FORBIDDEN)
        }
    }

    override fun addProfessionalToJobOffer(jobOffersId: Long, professionalId: Long): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        jobOffer.addProfessional(professional)

        logger.info("Professional $professionalId added to jobOffer $jobOffersId")
        return ResponseEntity("Professional $professionalId added to jobOffer $jobOffersId", HttpStatus.OK)

    }

    override fun removeProfessionalFromJobOffer(jobOffersId: Long, professionalId: Long): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        jobOffer.removeProfessional(professional)

        logger.info("Professional $professionalId removed from jobOffer $jobOffersId")
        return ResponseEntity("Professional $professionalId removed from jobOffer $jobOffersId", HttpStatus.OK)

    }

    override fun getAllOpenJobOffersOfCustomer(customerId: Long, page: Int, size: Int): ResponseEntity<List<JobOfferDTO>> {
        customerRepository.findByIdOrNull(customerId) ?: throw CustomerNotFoundException("Customer not found: $customerId")

        // Use parameters passed to the endpoint to filter the search in the DB
        val specifications = listOfNotNull(
            JobOfferSpecification.withCustomer(customerId),
            JobOfferSpecification.withStatus(JobOfferState.CREATED),
            JobOfferSpecification.withStatus(JobOfferState.SELECTION_PHASE),
            JobOfferSpecification.withStatus(JobOfferState.CANDIDATE_PROPOSAL)
        )

        val specs = if (specifications.isNotEmpty()) {
            specifications.reduce(Specification<JobOffer>::and)
        } else {
            Specification.where(null) // This acts as a no-op specification.
        }

        // Return the filtered search, also page it if necessary
        return ResponseEntity(jobOfferRepository.findAll(specs, PageRequest.of(page, size)).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun getAllAcceptedJobOffersOfProfessional(professionalId: Long, page: Int, size: Int): ResponseEntity<List<JobOfferDTO>> {
        professionalRepository.findByIdOrNull(professionalId) ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        // Use parameters passed to the endpoint to filter the search in the DB
        val specifications = listOfNotNull(
            JobOfferSpecification.withProfessional(professionalId),
            JobOfferSpecification.withStatus(JobOfferState.CONSOLIDATED),
            JobOfferSpecification.withStatus(JobOfferState.DONE)
        )

        val specs = if (specifications.isNotEmpty()) {
            specifications.reduce(Specification<JobOffer>::and)
        } else {
            Specification.where(null) // This acts as a no-op specification.
        }

        // Return the filtered search, also page it if necessary
        return ResponseEntity(jobOfferRepository.findAll(specs, PageRequest.of(page, size)).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun getAllRegistered(page: Int, size: Int, customerId: Long?, professionalId: Long?): ResponseEntity<List<JobOfferDTO>> {
        professionalId?.let {
            professionalRepository.findByIdOrNull(professionalId) ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")
        }

        customerId?.let {
            customerRepository.findByIdOrNull(customerId) ?: throw CustomerNotFoundException("Customer not found: $customerId")
        }

        // Use parameters passed to the endpoint to filter the search in the DB
        val specifications = listOfNotNull(
            JobOfferSpecification.withProfessional(professionalId),
            JobOfferSpecification.withCustomer(customerId)
        )

        val specs = if (specifications.isNotEmpty()) {
            specifications.reduce(Specification<JobOffer>::and)
        } else {
            Specification.where(null) // This acts as a no-op specification.
        }

        // Return the filtered search, also page it if necessary
        return ResponseEntity(jobOfferRepository.findAll(specs, PageRequest.of(page, size)).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun changeJobOfferStatus(jobOffersId: Long, status: String, note: String?, professionalId: Long?): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        val professional = professionalId?.let {
            professionalRepository.findByIdOrNull(it) ?: throw ProfessionalNotFoundException("Professional not found: $it")
        }

        val oldState = jobOffer.status
        val newState = JobOfferState.valueOf(status.uppercase())

        val validTransitions = mapOf(
            JobOfferState.CREATED to setOf(JobOfferState.SELECTION_PHASE, JobOfferState.ABORTED),
            JobOfferState.SELECTION_PHASE to setOf(JobOfferState.CANDIDATE_PROPOSAL, JobOfferState.ABORTED),
            JobOfferState.CANDIDATE_PROPOSAL to setOf(JobOfferState.CONSOLIDATED, JobOfferState.ABORTED, JobOfferState.SELECTION_PHASE),
            JobOfferState.CONSOLIDATED to setOf(JobOfferState.DONE, JobOfferState.ABORTED, JobOfferState.SELECTION_PHASE),
            JobOfferState.DONE to setOf(JobOfferState.SELECTION_PHASE)
        )

        if (!validTransitions[oldState]!!.contains(newState)) {
            throw JobOfferStateInvalidTransitionException("Cannot change state of JobOffer from $oldState to $newState")
        }

        // If we are in selection phase, we have found a candidate. Link it to the jobOffer
        if (jobOffer.status == JobOfferState.SELECTION_PHASE && newState == JobOfferState.CANDIDATE_PROPOSAL) {
            professional ?: return ResponseEntity("You should provide a professionalId", HttpStatus.BAD_REQUEST)
            jobOffer.addProfessional(professional)
        }

        // If we are in candidate proposal phase, we have to check if the candidate is eligible
        if(jobOffer.status == JobOfferState.CANDIDATE_PROPOSAL) {
            // Check if all the required skills for the jobOffer are present in the candidate
            if (newState == JobOfferState.CONSOLIDATED && (!jobOffer.professional!!.skills.containsAll(jobOffer.skills))) {
                jobOffer.removeProfessional(professional!!)
                jobOffer.status = JobOfferState.SELECTION_PHASE
                return ResponseEntity("Candidate $professionalId does not have all the skills required", HttpStatus.NOT_ACCEPTABLE)
            } else if (newState == JobOfferState.SELECTION_PHASE) {
                jobOffer.removeProfessional(professional!!)
            }
        }

        if (jobOffer.status == JobOfferState.CONSOLIDATED) {
            // If we return to the selection phase, unlink the professional
            if (newState == JobOfferState.SELECTION_PHASE) {
                jobOffer.removeProfessional(professional!!)
            } else {
                if (jobOffer.professional!!.employmentState == EmploymentState.UNEMPLOYED) {
                    // If we have to complete the job offer, set the professional as EMPLOYED
                    jobOffer.professional!!.changeEmploymentState(EmploymentState.EMPLOYED) // if it is consolidated, it surely has a professional linked to it
                }
                else {
                    return ResponseEntity("Candidate $professionalId is not UNEMPLOYED", HttpStatus.NOT_ACCEPTABLE)
                }
            }
        }

        // If we are returning from DONE to SELECTION_PHASE, unlink the professional
        if (jobOffer.status == JobOfferState.DONE) {
            jobOffer.removeProfessional(professional!!)
            jobOffer.professional!!.changeEmploymentState(EmploymentState.UNEMPLOYED)
        }

        // If we haven't returned yet, update the jobOffer state
        jobOffer.status = newState

        // Add a note to the jobOffer
        note?.let { jobOffer.addNotes(Note("${LocalDateTime.now()}: $note")) }

        logger.info("JobOffer $jobOffersId state changed from $oldState to $newState")
        return ResponseEntity("JobOffer $jobOffersId state changed successfully from $oldState to $newState", HttpStatus.OK)
    }


    override fun getState(jobOffersId: Long): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        return ResponseEntity(jobOffer.status.toString(), HttpStatus.OK)
    }

    override fun getJobOfferValue(jobOffersId: Long): ResponseEntity<String> {
        val jobOffer = jobOfferRepository.findByIdOrNull(jobOffersId)
            ?: throw JobOfferNotFoundException("JobOffer not found: $jobOffersId")

        return ResponseEntity(
            jobOffer.professional?.let { jobOffer.value } ?: "Not bounded to professional",
            HttpStatus.OK
        )

    }
}
