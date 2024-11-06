package com.example.crm_service.repositories.message

import com.example.crm_service.entities.message.Message
import com.example.crm_service.services.message.MessageState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<Message, Long> {
    fun findAllByState(state: MessageState, pageable: Pageable): Page<Message>
}