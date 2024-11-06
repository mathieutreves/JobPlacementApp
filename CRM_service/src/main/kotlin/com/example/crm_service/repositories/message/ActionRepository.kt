package com.example.crm_service.repositories.message

import com.example.crm_service.entities.message.Action
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActionRepository: JpaRepository<Action, Long>