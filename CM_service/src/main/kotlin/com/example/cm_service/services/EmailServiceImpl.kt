package com.example.cm_service.services

import com.example.cm_service.entities.EmailRequest
import com.google.api.services.gmail.model.Message
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    val camelContext: CamelContext,
    val producerTemplate: ProducerTemplate
) : EmailService {

    private val logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    /**
     * Sends an email using the sendEmails Camel route.
     *
     * @param emailRequest the email request containing recipient, subject, and body.
     * @return ResponseEntity containing the message ID of the email sent.
     */
    override fun sendEmail(emailRequest: EmailRequest): ResponseEntity<String> {
        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withBody(emailRequest)
            .build()
        producerTemplate.send("direct:sendEmails", exchange)
        return ResponseEntity(exchange.`in`.messageId, HttpStatus.OK)
    }

    /**
     * Retrieves emails based on specified criteria using the retrieveEmails Camel route.
     *
     * @param fullMessage flag to determine whether to retrieve the full message content.
     * @param unread flag to filter emails by their unread status.
     * @param sender optional parameter to filter emails by the sender's email address.
     * @param maxResults optional parameter to limit the number of results returned.
     * @param pageToken optional parameter to handle pagination of the results.
     * @param days optional parameter to filter emails by their age (in days).
     * @return ResponseEntity containing a list of emails matching the specified criteria.
     */
    override fun retrieveEmails(
        fullMessage: Boolean,
        unread: Boolean?,
        sender: String?,
        maxResults: Long?,
        pageToken: String?,
        days: Int?
    ): ResponseEntity<List<Message>> {
        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withHeader("fullMessage", fullMessage)
            .withHeader("unread", unread)
            .withHeader("sender", sender)
            .withHeader("maxResults", maxResults)
            .withHeader("pageToken", pageToken)
            .withHeader("days", days)
            .build()
        producerTemplate.send("direct:retrieveEmails", exchange)

        val emails = exchange.`in`.body as? List<Message>
        return if (emails != null) {
            ResponseEntity(emails, HttpStatus.OK)
        } else {
            ResponseEntity(emptyList(), HttpStatus.OK)
        }
    }

    /**
     * Deletes emails based on specified criteria using the deleteEmails Camel route.
     *
     * @param read flag to filter emails by their read status.
     * @param unread flag to filter emails by their unread status.
     * @param sender optional parameter to filter emails by the sender's email address.
     * @return ResponseEntity containing the status of the email deletion operation.
     */
    override fun deleteEmails(read: Boolean, unread: Boolean, sender: String?): ResponseEntity<String> {
        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withHeader("read", read)
            .withHeader("unread", unread)
            .withHeader("sender", sender)
            .build()
        producerTemplate.send("direct:deleteEmails", exchange)
        return ResponseEntity(exchange.`in`.body.toString(), HttpStatus.OK)
    }
}