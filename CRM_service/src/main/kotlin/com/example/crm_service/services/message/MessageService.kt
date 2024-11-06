package com.example.crm_service.services.message

import com.example.crm_service.dtos.message.ActionDTO
import com.example.crm_service.dtos.message.MessageDTO
import org.springframework.http.ResponseEntity

interface MessageService {
    fun getAllMessages(state: String?, sort: String, page: Int, size: Int): ResponseEntity<List<MessageDTO>>
    fun createMessage(sender: String, contactInfo: String?, channel: String, subject: String, body: String, priority: Long?): ResponseEntity<String>
    fun getMessageById(messageId: Long): ResponseEntity<MessageDTO>
    fun changeStateById(messageId: Long, newState: String, comment: String): ResponseEntity<String>
    fun getHistoryById(messageId: Long) : ResponseEntity<List<ActionDTO>>
    fun changePriorityById(messageId: Long, priority: Long): ResponseEntity<String>
}