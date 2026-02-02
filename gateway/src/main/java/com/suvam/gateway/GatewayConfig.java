package com.suvam.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product", r -> r
                        .path("/api/products/**")
                        .filters(f->f.circuitBreaker(config -> config
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products")))
                        .uri("lb://PRODUCT"))

                .route("user", r -> r
                        .path("/api/users/**")
                        .filters(f->f.circuitBreaker(config -> config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/users")))
                        .uri("lb://USER"))

                .route("order", r -> r
                        .path("/api/orders/**", "/api/cart/**")
                        .filters(f->f.circuitBreaker(config -> config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/orders")))
                        .uri("lb://ORDER"))

                .route("eureka", r -> r
                        .path("/eureka/main")
                        .uri("http://localhost:8761"))

                .route("eureka-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
