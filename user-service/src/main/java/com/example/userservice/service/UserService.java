package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.OrderResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
//    private final RestTemplate restTemplate;
//    private final RestClient.Builder restClientBuilder;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory<Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder> circuitBreakerFactory;

    @Transactional
    public UserResponse createUser(String email, String name, String password) {
        if(repository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일");
        }
        User user = new User(email, name, UUID.randomUUID().toString(), passwordEncoder.encode(password));
        repository.save(user);
        return UserResponse.from(user);
    }

    public UserResponse getUserByUserId(String userId) {
        User user = repository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        String orderUrl = env.getProperty("order_service.url").formatted(userId);
//        ResponseEntity<List<OrderResponse>> orderListResponse = restTemplate.exchange(
//                        orderUrl,
//                        HttpMethod.GET,
//                        null,
//                        new ParameterizedTypeReference<List<OrderResponse>>() {}
//        );
//        List<OrderResponse> orders = orderListResponse.getBody();

//        List<OrderResponse> orders = restClientBuilder.build()
//                                                      .get()
//                                                      .uri(orderUrl)
//                                                      .retrieve()
//                                                      .body(new ParameterizedTypeReference<List<OrderResponse>>() {});

//        List<OrderResponse> orders = null;
//        try {
//            orders = orderServiceClient.getOrders(userId);
//        } catch(FeignException e) {
//            log.error(e.getMessage());
//        }

        //ErrorDecoder
//        List<OrderResponse> orders = orderServiceClient.getOrders(userId);

        //https://martinfowler.com/bliki/CircuitBreaker.html
        log.info("Before call Order Microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        List<OrderResponse> orders = circuitBreaker.run(() -> orderServiceClient.getOrders(userId), throwable -> new ArrayList<>());
        log.info("After call Order Microservice");
        return UserResponse.from(user, orders);
    }

    public List<UserResponse> getUserAll() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse getUserByEmail(String username) {
        return UserResponse.from(repository.findByEmail(username).orElseThrow());
    }
}
