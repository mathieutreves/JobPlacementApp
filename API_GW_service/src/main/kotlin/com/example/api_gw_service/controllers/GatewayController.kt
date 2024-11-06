package com.example.api_gw_service.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDateTime

@RestController
@RequestMapping("/")
class GatewayController {

    /**
     * Endpoint to return the authentication principal.
     *
     * @param authentication the current authentication object, may be null if not authenticated.
     * @return a map containing the principal information.
     */
    @GetMapping("", "/")
    fun home(authentication: Authentication?): Map<String, Any?> {
        return mapOf(
            "principal" to authentication?.principal
        )
    }

    /**
     * Endpoint to return information about the current authenticated user.
     *
     * @param xsrf the XSRF token from the cookie, may be null.
     * @param authentication the current authentication object, may be null if not authenticated.
     * @return a map containing user details such as name, login URL, logout URL, principal, and XSRF token.
     */
    @GetMapping("/me")
    fun me(
        @CookieValue(name="XSRF-TOKEN", required = false)
        xsrf: String?,
        authentication: Authentication?
    ): Map<String, Any?> {
        val principal: OidcUser? = authentication?.principal as? OidcUser
        val name = principal?.preferredUsername ?: ""
        return mapOf(
            "name" to name,
            "loginUrl" to "/oauth2/authorization/API_GatewayClient",
            "logoutUrl" to "/logout",
            "principal" to principal,
            "xsrfToken" to xsrf
        )
    }

    /**
     * Endpoint to return secure information.
     *
     * @param principal the current authenticated principal.
     * @return a map containing the principal's name, current date and time, and principal information.
     */
    @GetMapping("/secure")
    fun secure(principal: Principal): Map<String, Any?> {
        return mapOf(
            "name" to "home",
            "date" to LocalDateTime.now(),
            "principal" to principal
        )
    }

    /**
     * Fallback endpoint for unauthorized GET requests.
     *
     * @return ResponseEntity with status UNAUTHORIZED and message "Not Authorized".
     */
    @GetMapping("/fallback/notAuthorized")
    fun fallbackNotAuthorizedOnGet() : ResponseEntity<String> {
        return ResponseEntity("Not Authorized", HttpStatus.UNAUTHORIZED)
    }

    /**
     * Fallback endpoint for unauthorized POST requests.
     *
     * @return ResponseEntity with status UNAUTHORIZED and message "Not Authorized".
     */
    @PostMapping("/fallback/notAuthorized")
    fun fallbackNotAuthorizedOnPost() : ResponseEntity<String> {
        return ResponseEntity("Not Authorized", HttpStatus.UNAUTHORIZED)
    }
}