package com.example.cm_service.routes

import com.example.cm_service.entities.EmailRequest
import com.google.api.services.gmail.model.Message
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.stereotype.Component
import java.util.*

@Component
class SendEmailsRoute : RouteBuilder() {

    @EndpointInject("google-mail:messages/send")
    lateinit var endpoint: GoogleMailEndpoint

    override fun configure() {
        // Route to handle sending email
        from("direct:sendEmails")
            .process { exchange ->
                val email = exchange.`in`.getBody(EmailRequest::class.java)
                val message = endpoint.client.users().messages().send("me", createMessageWithEmail(email)).execute()
                exchange.`in`.messageId = message.id
            }
    }

    /**
     * Creates a Message object with the given email details.
     *
     * @param email the EmailRequest object containing email details
     * @return a Message object ready to be sent
     */
    private fun createMessageWithEmail(email: EmailRequest): Message {
        // Construct the raw email content
        val emailContent = """
            From: "g09.webapp2@gmail.com"
            To: ${email.to}
            Subject: ${email.subject}

            ${email.body}
        """.trimIndent()

        // Encode the email content to base64 URL-safe format
        val encodedEmail = Base64.getUrlEncoder().encodeToString(emailContent.toByteArray())
        // Create and return the Message object with the encoded email content
        return Message().setRaw(encodedEmail)
    }
}