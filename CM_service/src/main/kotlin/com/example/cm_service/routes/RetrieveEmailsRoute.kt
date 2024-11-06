package com.example.cm_service.routes

import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.stereotype.Component
import java.util.*

@Component
class RetrieveEmailsRoute: RouteBuilder() {

    @EndpointInject("google-mail:messages/list")
    lateinit var listEndpoint: GoogleMailEndpoint

    @EndpointInject("google-mail:messages/get")
    lateinit var getEndpoint: GoogleMailEndpoint

    override fun configure() {
        // Route to handle retrieving emails
        from("direct:retrieveEmails")
            .process { exchange ->
                val retrieveFullMessage = exchange.`in`.getHeader("fullMessage") as Boolean
                val unread = exchange.`in`.getHeader("unread") as Boolean?
                val sender = exchange.`in`.getHeader("sender") as String?
                val maxResults = exchange.`in`.getHeader("maxResults") as Long?
                val pageToken = exchange.`in`.getHeader("pageToken") as String?
                val days = exchange.`in`.getHeader("days") as Int?

                // Create the query for filtering emails
                val query = StringBuilder()
                if (unread == true) {
                    query.append("is:unread ")
                }
                if (!sender.isNullOrEmpty()) {
                    query.append("from:$sender ")
                }
                if (days != null) {
                    val maxDate = System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L
                    val formattedDate = java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochMilli(maxDate))
                    query.append("after:$formattedDate ")
                }

                // List messages based on the query
                val listRequest = listEndpoint.client.users().messages().list("me").setQ(query.toString())
                if (maxResults != null) {
                    listRequest.maxResults = maxResults
                }
                if (!pageToken.isNullOrEmpty()) {
                    listRequest.pageToken = pageToken
                }

                val listResponse: ListMessagesResponse = listRequest.execute()
                val messages: List<Message> = listResponse.messages ?: emptyList()

                // Fetch the full details of each message
                val fullMessages = messages.map { getEndpoint.client.users().messages().get("me", it.id).execute() }

                // Extract principal information
                val principalInfo = fullMessages.map { message ->
                    val headers = message.payload.headers
                    val principalHeaders = headers.filter { header ->
                        header.name in listOf("Delivered-To", "From", "Date", "Subject", "To")
                    }.associate { it.name to it.value }

                    val parts = message.payload.parts
                    val bodyText = parts?.find { it.mimeType == "text/plain" }?.body?.data?.let { decodeBase64(it) }

                    mapOf(
                        "historyId" to message.historyId,
                        "id" to message.id,
                        "internalDate" to message.internalDate,
                        "labelIds" to message.labelIds,
                        "headers" to principalHeaders,
                        "bodyText" to bodyText,
                        "snippet" to message.snippet
                    )
                }

                // Set the exchange body based on the retrieveFullMessage flag
                exchange.`in`.body = if (retrieveFullMessage) fullMessages else principalInfo
                exchange.`in`.setHeader("nextPageToken", listResponse.nextPageToken)
            }
    }

    /**
     * Decodes a base64 encoded string, handling URL-safe encoding.
     *
     * @param encodedString the base64 encoded string
     * @return the decoded string
     */
    private fun decodeBase64(encodedString: String): String {
        // Handle URL-safe base64 encoding
        val base64String = encodedString.replace('-', '+').replace('_', '/')
        // Add padding if necessary
        val padding = "=".repeat((4 - base64String.length % 4) % 4)
        return String(Base64.getDecoder().decode(base64String + padding))
    }
}