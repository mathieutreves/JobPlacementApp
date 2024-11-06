package com.example.crm_service.repositories.jobOffer

import com.example.crm_service.entities.jobOffer.Skill
import org.springframework.data.jpa.repository.JpaRepository

interface SkillRepository: JpaRepository<Skill, Long>{
    fun findByName(name: String): Skill?
}