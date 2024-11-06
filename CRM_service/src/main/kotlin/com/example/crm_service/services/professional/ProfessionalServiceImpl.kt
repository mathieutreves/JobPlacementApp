package com.example.crm_service.services.professional

import com.example.crm_service.dtos.professional.ProfessionalDTO
import com.example.crm_service.dtos.professional.toDTO
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.jobOffer.Skill
import com.example.crm_service.entities.professional.Professional
import com.example.crm_service.exceptions.contact.ContactNotFoundException
import com.example.crm_service.exceptions.professional.*
import com.example.crm_service.repositories.NoteRepository
import com.example.crm_service.repositories.contact.ContactRepository
import com.example.crm_service.repositories.jobOffer.SkillRepository
import com.example.crm_service.repositories.professional.ProfessionalRepository
import com.example.crm_service.repositories.professional.ProfessionalSpecification
import com.example.crm_service.services.contact.ContactServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfessionalServiceImpl(
    private val professionalRepository: ProfessionalRepository,
    private val contactRepository: ContactRepository,
    private val skillRepo: SkillRepository,
    private val noteRepo: NoteRepository
): ProfessionalService {

    private val logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)

    override fun getAllProfessionals(skill: String?, location: String?, employmentState: String?, page: Int, size: Int) : ResponseEntity<List<ProfessionalDTO>> {
        val pageable = PageRequest.of(page, size)

        // Use parameters passed to the endpoint to filter the search in the DB
        val specifications = listOfNotNull(
            ProfessionalSpecification.withSkill(skill),
            ProfessionalSpecification.withLocation(location),
            ProfessionalSpecification.withEmploymentState(employmentState?.let { EmploymentState.valueOf(it.uppercase()) } )
        )

        val specs = if (specifications.isNotEmpty()) {
            specifications.reduce(Specification<Professional>::and)
        } else {
            Specification.where(null) // This acts as a no-op specification.
        }

        // Return the filtered search, also page it if necessary
        return ResponseEntity(professionalRepository.findAll(specs, pageable).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun addProfessional(name: String, surname: String, employmentState: String, location: String, dailyRate: String): ResponseEntity<String> {
        val contact = contactRepository.findByNameAndSurname(name, surname)
            ?: throw ContactNotFoundException("You have to create the contact information before adding them as a professional figure")

        if (professionalRepository.findByNameAndSurname(name, surname) != null) {
            return ResponseEntity("Professional already exists", HttpStatus.CONFLICT)
        }

        val newProfessional = Professional(name, surname, location, dailyRate, EmploymentState.valueOf(employmentState.uppercase()))

        // Link this professional profile to the contact
        contact.category = when (contact.category) {
            "CUSTOMER" -> "BOTH"
            else -> "PROFESSIONAL"
        }
        contact.professionalProfile = newProfessional
        newProfessional.contact = contact

        professionalRepository.save(newProfessional)
        logger.info("Professional created successfully with id ${newProfessional.id}: $name $surname")
        return ResponseEntity("Professional created successfully", HttpStatus.CREATED)
    }

    override fun addSkillToProfessional(professionalId: Long, newSkill: String): ResponseEntity<String> {
        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        var responseStatus = HttpStatus.OK
        val skill = skillRepo.findByName(newSkill) ?: Skill(newSkill).also { skillRepo.save(it) }
            .also { logger.info("Created new skill with id ${it.id}") }.also { responseStatus = HttpStatus.CREATED }
        professional.addSkill(skill)
        logger.info("Skill $newSkill added successfully to professional $professionalId with id: ${skill.id}")
        return ResponseEntity("Skill added successfully", responseStatus)
    }

    override fun deleteSkillFromProfessional(professionalId: Long, skillId: Long): ResponseEntity<String> {
        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        val skillToDelete = skillRepo.findByIdOrNull(skillId)
            ?: throw SkillNotFoundException("Skill not found: $skillId")

        // Check if the skill is actually associated to this contact
        return if (professional.skills.contains(skillToDelete)) {
            // Remove skill from contact and vice versa
            professional.removeSkill(skillToDelete)

            // Check if the skill has other contact associated to it. If not, remove it from the DB.
            if(skillToDelete.professionals.isEmpty() && skillToDelete.jobOffers.isEmpty()){
                skillRepo.deleteById(skillId)
                logger.info("Email with id $skillId removed from the DB")
            }

            logger.info("Skill $skillToDelete removed successfully from contact $professionalId")
            ResponseEntity("Skill removed successfully from professional", HttpStatus.OK)
        } else {
            ResponseEntity("This skill is not associated to the professional", HttpStatus.FORBIDDEN)
        }
    }

    override fun addNoteToProfessional(professionalId: Long, newNote: String): ResponseEntity<String> {
        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        val note = Note(newNote).also { professional.addNote(it) }
        logger.info("Note $newNote added successfully to professional $professionalId with id: ${note.id}")
        return ResponseEntity("Note added successfully", HttpStatus.CREATED)
    }

    override fun deleteNoteFromProfessional(professionalId: Long, noteId: Long): ResponseEntity<String> {
        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        val noteToDelete = noteRepo.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException("Note not found: $noteId")

        professional.removeNote(noteToDelete)
        logger.info("Note $noteId deleted successfully from professional $professionalId with id: ${noteToDelete.id}")
        return ResponseEntity("Note deleted successfully", HttpStatus.OK)
    }

    override fun changeEmploymentStateById(professionalId: Long, newEmploymentState: String): ResponseEntity<String> {
        val professional = professionalRepository.findByIdOrNull(professionalId)
            ?: throw ProfessionalNotFoundException("Professional not found: $professionalId")

        val oldState = professional.employmentState
        val newState = EmploymentState.valueOf(newEmploymentState.uppercase())

        professional.employmentState = newState
        logger.info("Employment state of professional $professionalId changed from $oldState to $newState")
        return ResponseEntity("Employment state changed successfully", HttpStatus.OK)
    }
}