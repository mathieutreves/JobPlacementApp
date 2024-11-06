package com.example.crm_service.services.professional

import com.example.crm_service.dtos.professional.ProfessionalDTO
import org.springframework.http.ResponseEntity

interface ProfessionalService {
    fun getAllProfessionals(skill: String?, location: String?, employmentState: String?, page: Int, size: Int) : ResponseEntity<List<ProfessionalDTO>>
    fun addProfessional(name: String, surname: String, employmentState: String, location: String, dailyRate: String) : ResponseEntity<String>
    fun addSkillToProfessional(professionalId: Long, newSkill: String) : ResponseEntity<String>
    fun deleteSkillFromProfessional(professionalId: Long, skillId: Long) : ResponseEntity<String>
    fun addNoteToProfessional(professionalId: Long, newNote: String) : ResponseEntity<String>
    fun deleteNoteFromProfessional(professionalId: Long, noteId: Long) : ResponseEntity<String>
    fun changeEmploymentStateById(professionalId: Long, newEmploymentState: String) : ResponseEntity<String>
}