package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.OrderResponseDTO;
import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    //private final RestClient restClient;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreaker circuitBreaker;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(UserResponseDTO::from).toList();
    }

    @Transactional
    public UserResponseDTO save(UserRequestDTO user) {
        return UserResponseDTO.from(userRepository.save(UserEntity.builder()
                                                                  .userId(user.userId())
                                                                  .name(user.name())
                                                                  .password(passwordEncoder.encode(user.password()))
                                                                  .build()));
    }

    public UserResponseDTO findByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

//        String orderUrl = String.format(Objects.requireNonNull(env.getProperty("order-service.url")), userId);
//        List<OrderResponseDTO> orders = restClient.get()
//                                                  .uri(orderUrl)
//                                                  .retrieve()
//                                                  .body(new ParameterizedTypeReference<>() {});

//        List<OrderResponseDTO> orders = null;
//        try {
//            orders = orderServiceClient.getOrder(userId);
//        } catch(FeignException e) {
//            log.error(e.getMessage());
//        }

        log.info("Before call order service");
        List<OrderResponseDTO> orders = circuitBreaker.run(() -> orderServiceClient.getOrder(userId), throwable -> {
            log.error("CircuitBreaker Fallback", throwable);
            return new ArrayList<>();
        });
        log.info("After call order service");
        return UserResponseDTO.from(user, orders);
    }

    public String login(String userId, String password) {
        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        byte[] secretKeyBytes = Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        return Jwts.builder()
                   .subject(userId)
                   .expiration(Date.from(now.plusMillis(Long.parseLong(Objects.requireNonNull(env.getProperty("token.expiration-time"))))))
                   .issuedAt(Date.from(now))
                   .signWith(secretKey)
                   .compact();
    }
}
