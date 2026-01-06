package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                                         .filters(f -> f.addRequestHeader("f-request", "f-request-java")
                                                                       .addResponseHeader("f-response", "f-response-java"))
                                         .uri("http://localhost:8081")
                )
                .route(r -> r.path("/second-service/**")
                                         .filters(f -> f.addRequestHeader("s-request", "s-request-java")
                                                                       .addResponseHeader("s-response", "s-response-java"))
                                         .uri("http://localhost:8082")
                )
                .build();
    }
}
