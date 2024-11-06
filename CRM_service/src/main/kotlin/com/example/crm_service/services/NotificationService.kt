package com.example.crm_service.services

import com.example.crm_service.services.message.MessageState
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class NotificationService(
    private val restTemplate: RestTemplate,
    @Value("\${keycloak.auth-server-url}") private val authServerUrl: String,
    @Value("\${keycloak.realm}") private val realm: String,
    @Value("\${keycloak.client-id}") private val clientId: String,
    @Value("\${keycloak.client-secret}") private val clientSecret: String
) {

    private fun getKeycloakToken(): String {
        val keycloak: Keycloak = KeycloakBuilder.builder()
            .serverUrl(authServerUrl)
            .realm(realm)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build()

        val tokenResponse = keycloak.tokenManager().accessToken
        return tokenResponse.token
    }

    fun notifyEmailService(messageId: Long, oldMessageState: MessageState, newMessageState: MessageState, recipient: String) {
        val url = System.getenv("CM_NOTIFY_URI")
        val headers = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
            setBearerAuth(getKeycloakToken())
        }

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("to", recipient)
        body.add("subject", "Message State Change Notification")
        body.add("body", "Message $messageId state changed from $oldMessageState to $newMessageState")

        val request = HttpEntity(body, headers)
        restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
    }
}
