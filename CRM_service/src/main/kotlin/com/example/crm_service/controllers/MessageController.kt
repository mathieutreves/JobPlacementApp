package com.example.crm_service.controllers

import com.example.crm_service.dtos.message.ActionDTO
import com.example.crm_service.dtos.message.MessageDTO
import com.example.crm_service.services.message.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/messages")
class MessageController (private val messageService: MessageService) {

    @GetMapping("/", "")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllMessages(
        @RequestParam(required = false) state: String?,
        @RequestParam(required = false, defaultValue = "id,desc") sort: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<MessageDTO>> {
        return messageService.getAllMessages(state, sort, page, size)
    }

    @PostMapping("/", "")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun createMessage(
        @RequestParam("sender") sender: String,
        @RequestParam("contactInfo", required = false) contactInfo: String?,
        @RequestParam("channel") channel: String,
        @RequestParam("subject") subject: String,
        @RequestParam("body") body: String,
        @RequestParam("priority", required = false) priority: Long?
    ) : ResponseEntity<String> {
        return messageService.createMessage(sender, contactInfo, channel, subject, body, priority)
    }

    @GetMapping("/{messageId}")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getMessageById(
        @PathVariable("messageId") messageId: Long
    ) : ResponseEntity<MessageDTO> {
        return messageService.getMessageById(messageId)
    }

    @PostMapping("/{messageId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeStateById(
        @PathVariable("messageId") messageId: Long,
        @RequestParam("state") newState: String,
        @RequestParam("comment") comment: String
    ) : ResponseEntity<String> {
        return messageService.changeStateById(messageId, newState, comment)
    }

    @GetMapping("/{messageId}/history")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getHistoryById(
        @PathVariable("messageId") messageId: Long
    ) : ResponseEntity<List<ActionDTO>> {
        return messageService.getHistoryById(messageId)
    }

    @PutMapping("/{messageId}/priority")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changePriorityById(
        @PathVariable("messageId") messageId: Long,
        @RequestParam("priority") newPriority: Long
    ) : ResponseEntity<String> {
        return messageService.changePriorityById(messageId, newPriority)
    }

}