package com.example.userservice.config.restclient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    //http://localhost:8000/order-service/%s/orders -> http://order-service/%s/orders
    @Bean
    public RestClient restClient() {
        return restClientBuilder().requestInterceptor(new RestClientInterceptor()).build();
    }
}
