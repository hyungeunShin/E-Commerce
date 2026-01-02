package com.example.userservice.controller;

import com.example.userservice.dto.RequestUserDTO;
import com.example.userservice.dto.ResponseUserDTO;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final Environment env;
    private final UserService userService;

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in User Service, port(local.server.port)=%s, port(server.port)=%s".formatted(env.getProperty("local.server.port"), env.getProperty("server.port"));
    }

    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request) {
        log.info("users.welcome ip: {}, {}, {}, {}", request.getRemoteAddr(), request.getRemoteHost(), request.getRequestURI(), request.getRequestURL());
        return "Welcome to the E-Commerce";
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUserDTO> createUser(@RequestBody @Validated RequestUserDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ResponseUserDTO.from(userService.createUser(user)));
    }
}