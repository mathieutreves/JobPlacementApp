package com.example.crm_service.dtos.jobOffer

import com.example.crm_service.entities.jobOffer.Skill

data class SkillDTO(
    val id: Long,
    val name: String
)

fun Skill.toDTO(): SkillDTO =
    SkillDTO(this.id, this.name)