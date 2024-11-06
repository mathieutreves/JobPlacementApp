package com.example.cm_service.controllers

import com.example.cm_service.entities.EmailRequest
import com.example.cm_service.services.EmailService
import com.google.api.services.gmail.model.Message
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/emails")
class EmailController (private val emailService: EmailService) {

    /**
     * Endpoint to send an email.
     *
     * @param to the recipient's email address.
     * @param subject the subject of the email.
     * @param body the body content of the email.
     * @return ResponseEntity containing the status of the email sending operation.
     */
    @PostMapping("", "/", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun sendEmail(
        @RequestPart to: String,
        @RequestPart subject: String,
        @RequestPart body: String,
    ): ResponseEntity<String> {
        return emailService.sendEmail(EmailRequest(to, subject, body))
    }

    /**
     * Endpoint to retrieve emails.
     *
     * @param fullMessage flag to determine whether to retrieve the full message content.
     * @param unread flag to filter emails by their unread status.
     * @param sender optional parameter to filter emails by the sender's email address.
     * @param maxResults optional parameter to limit the number of results returned.
     * @param pageToken optional parameter to handle pagination of the results.
     * @param days optional parameter to filter emails by their age (in days).
     * @return ResponseEntity containing a list of emails matching the specified criteria.
     */
    @GetMapping("", "/")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getEmails(
        @RequestParam("fullMessage", required = false, defaultValue = "false") fullMessage: Boolean,
        @RequestParam("unread", required = false) unread: Boolean?,
        @RequestParam("sender", required = false) sender: String?,
        @RequestParam("maxResults", required = false) maxResults: Long?,
        @RequestParam("pageToken", required = false) pageToken: String?,
        @RequestParam("days", required = false) days: Int?,
    ): ResponseEntity<List<Message>> {
        return emailService.retrieveEmails(fullMessage, unread, sender, maxResults, pageToken, days)
    }

    /**
     * Endpoint to delete emails.
     *
     * @param read flag to filter emails by their read status.
     * @param unread flag to filter emails by their unread status.
     * @param sender optional parameter to filter emails by the sender's email address.
     * @return ResponseEntity containing the status of the email deletion operation.
     */
    @DeleteMapping("", "/")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteEmails(
        @RequestParam("read", required = true) read: Boolean,
        @RequestParam("unread", required = true) unread: Boolean,
        @RequestParam("sender", required = false) sender: String?
    ): ResponseEntity<String> {
        return emailService.deleteEmails(read, unread, sender)
    }
}