package com.example.crm_service.controllers

import com.example.crm_service.dtos.professional.ProfessionalDTO
import com.example.crm_service.services.professional.ProfessionalService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/professionals")
class ProfessionalController(private val professionalService: ProfessionalService) {

    @GetMapping("/", "")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllProfessionals(
        @RequestParam("skill", required = false) skill: String?,
        @RequestParam("location", required = false) location: String?,
        @RequestParam("employmentState", required = false) employmentState: String?,
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<ProfessionalDTO>> {
        return professionalService.getAllProfessionals(skill, location, employmentState, page, size)
    }

    @PostMapping("/", "")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addProfessionals(
        @RequestParam("name") name: String,
        @RequestParam("surname") surname: String,
        @RequestParam("employmentState") employmentState: String,
        @RequestParam("location") location: String,
        @RequestParam("dailyRate") dailyRate: String
    ) : ResponseEntity<String> {
        return professionalService.addProfessional(name, surname, employmentState, location, dailyRate)
    }

    @PostMapping("/{professionalId}/skills")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addSkillToProfessional(
        @PathVariable("professionalId") professionalId: Long,
        @RequestParam("skill") newSkill: String
    ) : ResponseEntity<String> {
        return professionalService.addSkillToProfessional(professionalId, newSkill)
    }

    @DeleteMapping("/{professionalId}/skills/{skillId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteSkillFromProfessional(
        @PathVariable("professionalId") professionalId: Long,
        @PathVariable("skillId") skillId: Long
    ) : ResponseEntity<String> {
        return professionalService.deleteSkillFromProfessional(professionalId, skillId)
    }

    @PostMapping("/{professionalId}/notes")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addNoteToProfessional(
        @PathVariable("professionalId") professionalId: Long,
        @RequestParam("note") note: String
    ) : ResponseEntity<String> {
        return professionalService.addNoteToProfessional(professionalId, note)
    }

    @DeleteMapping("/{professionalId}/notes/{noteId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteNoteFromProfessional(
        @PathVariable("professionalId") professionalId: Long,
        @PathVariable("noteId") noteId: Long
    ) : ResponseEntity<String> {
        return professionalService.deleteNoteFromProfessional(professionalId, noteId)
    }

    @PostMapping("/{professionalId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeEmploymentStateById(
        @PathVariable("professionalId") professionalId: Long,
        @RequestParam("employmentState") newEmploymentState: String
    ) : ResponseEntity<String> {
        return professionalService.changeEmploymentStateById(professionalId, newEmploymentState)
    }

}