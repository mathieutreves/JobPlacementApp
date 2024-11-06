package com.example.cm_service.services

import com.example.cm_service.entities.EmailRequest
import com.google.api.services.gmail.model.Message
import org.springframework.http.ResponseEntity

interface EmailService {
    fun sendEmail(emailRequest: EmailRequest): ResponseEntity<String>
    fun retrieveEmails(fullMessage: Boolean,unread: Boolean?, sender: String?, maxResults: Long?, pageToken: String?, days: Int?): ResponseEntity<List<Message>>
    fun deleteEmails(read: Boolean, unread: Boolean, sender: String?): ResponseEntity<String>
}