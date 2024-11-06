package com.example.api_gw_service.router

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.redirectTo
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
import java.net.URI

@Configuration
class RouterConfig {

    @Bean
    fun uiRouteConfig(): RouterFunction<ServerResponse> {
        return route("UI")
            .route(path("/ui/**"), http(System.getenv("SPA_URI")))
            .build()
    }

    @Bean
    fun homeRouteConfig(): RouterFunction<ServerResponse> {
        return route("HOME")
            .route(path("/"), http("http://localhost:8088"))
            .filter(redirectTo(301, URI.create("http://localhost:8088/ui")))
            .build()
    }

    @Bean
    fun loginRouteConfig(): RouterFunction<ServerResponse> {
        return route("HOME")
            .route(path("/login/**"), http("http://localhost:8088"))
            .filter(redirectTo(301, URI.create("http://localhost:8088/ui")))
            .build()
    }

    @Bean
    fun crmRouteConfig(): RouterFunction<ServerResponse> {
        return route("CRM_service")
            .route(path("/api/v1/**"), http(System.getenv("CRM_URI")))
            .filter(tokenRelay())
            .filter(circuitBreaker { config ->
                config.setId("crmCircuitBreaker")
                    .setFallbackUri("forward:/fallback/notAuthorized")
                    .setStatusCodes("403", "401")
            })
            .before(stripPrefix(2))
            .build()
    }

    @Bean
    fun documentStoreRouteConfig(): RouterFunction<ServerResponse> {
        return route("DS_service")
            .route(path("/api/v1/**"), http(System.getenv("DS_URI")))
            .filter(tokenRelay())
            .filter(circuitBreaker { config ->
                config.setId("dsCircuitBreaker")
                    .setFallbackUri("forward:/fallback/notAuthorized")
                    .setStatusCodes("403", "401")
            })
            .before(stripPrefix(2))
            .build()
    }

    @Bean
    fun communicationManagerRouteConfig(): RouterFunction<ServerResponse> {
        return route("CM_service")
            .route(path("/api/v1/**"), http(System.getenv("CM_URI")))
            .filter(tokenRelay())
            .filter(circuitBreaker { config ->
                config.setId("cmCircuitBreaker")
                    .setFallbackUri("forward:/fallback/notAuthorized")
                    .setStatusCodes("403", "401")
            })
            .before(stripPrefix(2))
            .build()
    }
}