package com.example.cm_service.routes

import com.example.cm_service.entities.Email
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange.*
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.core5.http.ContentType
import org.springframework.stereotype.Component


@Component
class AutoGetEmailsRoute : RouteBuilder() {

    @EndpointInject("google-mail:messages/get")
    lateinit var endpoint: GoogleMailEndpoint

    override fun configure() {

        var accessToken = ""

        // Step 1: Retrieve the access token
        from("timer://token?repeatCount=1&delay=0")
            .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
            .setBody(constant("client_id=${System.getenv("CLIENT_ID")}&client_secret=${System.getenv("CLIENT_SECRET")}&grant_type=client_credentials"))
            .to(System.getenv("KC_TOKEN_URI"))
            .unmarshal().json(JsonLibrary.Jackson, TokenResponse::class.java)
            .process { exchange ->
                val tokenResponse = exchange.getIn().getBody(TokenResponse::class.java)
                accessToken = tokenResponse.access_token
            }

        // Step 2: Process emails and send them to the CRM service
        from("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com")
            .process { it ->
                val id = it.`in`.getHeader("CamelGoogleMailId").toString()
                val message = endpoint.client.users().messages().get("me", id).execute()

                // Extract important headers from the message
                val headers = message.payload.headers
                val from = headers.find { it.name.equals("From", true) }?.value?.toString() ?: ""
                val to = headers.find { it.name.equals("To", true) }?.value?.toString() ?: ""
                val subject = headers.find { it.name.equals("Subject", true) }?.value?.toString() ?: ""

                val (name, fromEmail) = extractNameAndEmail(from)
                val email = Email(fromEmail, to, subject, message.snippet, null)

                // Build a multipart entity with email details to send to the CRM service
                val entity: MultipartEntityBuilder = MultipartEntityBuilder.create()
                entity.addTextBody("sender", email.from, ContentType.DEFAULT_TEXT)
                entity.addTextBody("channel", "EMAIL", ContentType.DEFAULT_TEXT)
                entity.addTextBody("subject", email.subject, ContentType.DEFAULT_TEXT)
                entity.addTextBody("body", email.body, ContentType.DEFAULT_TEXT)
                entity.addTextBody("contactInfo", name, ContentType.DEFAULT_TEXT)

                // Set the built entity as the body of the exchange
                it.`in`.body = entity.build()
                it.`in`.setHeader("Authorization", "Bearer $accessToken")
            }
            // Set HTTP headers for the request
            .setHeader(HTTP_PATH, constant("/API/messages"))
            .setHeader(HTTP_METHOD, constant("POST"))
            .setHeader(CONTENT_TYPE, constant("multipart/form-data"))
            // Log the request details for debugging purposes
            .to("log:DEBUG?showHeaders=true&showBody=true&multiline=true")
            // Send the request to the CRM service
            .toD(System.getenv("CRM_URI"))
    }

    // Inner class to hold the token response
    data class TokenResponse(
        @JsonProperty("access_token") val access_token: String,
        @JsonProperty("expires_in") val expires_in: Int,
        @JsonProperty("refresh_expires_in") val refresh_expires_in: Int,
        @JsonProperty("refresh_token") val refresh_token: String?,
        @JsonProperty("token_type") val token_type: String,
        @JsonProperty("not-before-policy") val not_before_policy: Int,
        @JsonProperty("session_state") val session_state: String?,
        @JsonProperty("scope") val scope: String,
    )

    /**
     * Extracts the name and email address from a formatted string.
     *
     * @param input the input string in the format "Name Surname (or other identifier) <email@example.com>"
     * @return a pair containing the extracted name and email address
     * @throws IllegalArgumentException if the input string is not in the expected format
     */
    fun extractNameAndEmail(input: String): Pair<String, String> {
        val regex = Regex("(.+) <(.+)>")
        val matchResult = regex.find(input)

        return if (matchResult != null) {
            val (name, email) = matchResult.destructured
            Pair(name, email)
        } else {
            throw IllegalArgumentException("Input string is not in the expected format")
        }
    }
}