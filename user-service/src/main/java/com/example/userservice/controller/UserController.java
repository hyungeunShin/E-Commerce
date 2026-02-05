package com.example.userservice.controller;

import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final Environment env;
    private final UserService userService;

    @GetMapping("/health-check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        //return "It's Working in User Service, port(local.server.port)=%s, port(server.port)=%s".formatted(env.getProperty("local.server.port"), env.getProperty("server.port"));

        return """
        [User Service Status]
        - local.server.port: %s
        - server.port:       %s
        - token secret:      %s
        - token expiration:  %s
        - config test:       %s
        - order-service.ulr: %s
        - build test
        """.formatted(env.getProperty("local.server.port"),
                      env.getProperty("server.port"),
                      env.getProperty("token.secret"),
                      env.getProperty("token.expiration-time"),
                      env.getProperty("config.test"),
                      env.getProperty("order-service.url")
        );
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome(HttpServletRequest request) {
        log.info("users.welcome ip: {}, {}, {}, {}", request.getRemoteAddr(), request.getRemoteHost(), request.getRequestURI(), request.getRequestURL());
        return "Welcome to the E-Commerce";
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Validated UserRequestDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userService.save(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(userService.findByUserId(userId));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Validated LoginRequestDTO dto) {
        String token = userService.login(dto.userId(), dto.password());
        log.info("Custom Login");
        return ResponseEntity.status(HttpStatus.OK)
                             .header(HttpHeaders.AUTHORIZATION, token)
                             .build();
    }
}