package com.example.crm_service.repositories.professional

import com.example.crm_service.entities.jobOffer.Skill
import com.example.crm_service.entities.professional.Professional
import com.example.crm_service.services.professional.EmploymentState
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification

class ProfessionalSpecification {
    companion object {

        fun withSkill(skill: String?): Specification<Professional>? {
            return skill?.let {
                Specification<Professional> { root, _, cb ->
                    val join = root.join<Professional, Skill>("skills", JoinType.INNER)
                    cb.like(join.get("name"), "%$it%")
                }
            }
        }

        fun withLocation(location: String?): Specification<Professional>? {
            return location?.let {
                Specification<Professional> { root, _, cb ->
                    cb.like(root.get("location"), "%$it%")
                }
            }
        }

        fun withEmploymentState(employmentState: EmploymentState?): Specification<Professional>? {
            return employmentState?.let {
                Specification<Professional> { root, _, cb ->
                    cb.like(root.get("employmentState"), employmentState.name)
                }
            }
        }
    }
}