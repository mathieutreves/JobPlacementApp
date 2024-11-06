package com.example.cm_service.routes

import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.stereotype.Component

@Component
class DeleteEmailsRoute : RouteBuilder() {

    @EndpointInject("google-mail:messages/list")
    lateinit var listEndpoint: GoogleMailEndpoint

    @EndpointInject("google-mail:messages/delete")
    lateinit var deleteEndpoint: GoogleMailEndpoint

    override fun configure() {
        // Route to handle deleting email
        from("direct:deleteEmails")
            .process { exchange ->
                // Retrieve all the headers in the exchange, so we can use them to filter the emails we want to delete
                val read = exchange.`in`.getHeader("read") as Boolean
                val unread = exchange.`in`.getHeader("unread") as Boolean
                val sender = exchange.`in`.getHeader("sender") as String?

                // Create the query for filtering emails
                val query = StringBuilder()
                if (read) { query.append("is:read ") }
                if (unread) { query.append("is:unread ") }
                if (sender != null) {
                    if (sender.isNotEmpty()) { query.append("from:$sender ") }
                }

                // List messages based on the query
                val listResponse: ListMessagesResponse = listEndpoint.client.users().messages().list("me").setQ(query.toString()).execute()
                val messages: List<Message> = listResponse.messages ?: emptyList()

                // Delete each message found
                for (message in messages) {
                    deleteEndpoint.client.users().messages().delete("me", message.id).execute()
                }

                exchange.`in`.body = "Deleted ${messages.size} emails"
            }
    }
}