package com.example.userservice.controller;

import com.example.userservice.dto.CreateUser;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @Value("${greeting.message}")
    private String message;

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return message;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Validated CreateUser dto) {
        return new ResponseEntity<>(UserResponse.from(service.createUser(dto.getEmail(), dto.getName(), dto.getPassword())), HttpStatus.CREATED);
    }
}
