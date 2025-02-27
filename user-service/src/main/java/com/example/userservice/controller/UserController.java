package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final Environment env;

    @Value("${greeting.message}")
    private String message;

    /*
    config-service 이후 설정 값이 바뀐다면
    1. 서버 재기동
    2. Actuator refresh
        application.yml 에 actuator 정보 추가 후 localhost:[port]/actuator/refresh 로 POST 요청
    3. Spring cloud bus
        - 분산 시스템의 노드(Micro Service)를 경량 메시지 브로커(RabbitMQ)와 연결
        - 상태 및 구성에 대한 변경 사항을 연결된 노드에게 전달
        user-service -> localhost:[port]/user-service/actuator/busrefresh
        api-gateway -> localhost:8000/actuator/busrefresh
    */
    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status(HttpServletRequest request) {
        return ("It's Working in User Service on PORT %s" +
                ", server port = %s" +
                ", test1 = %s" +
                ", test2 = %s" +
                ", token secret = %s" +
                ", token expiration time = %s")
                .formatted(request.getServerPort(), env.getProperty("server.port"),
                        env.getProperty("test.test1"), env.getProperty("test.test2"),
                        env.getProperty("token.secret"), env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
        return message;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Validated CreateUserRequest dto) {
        return new ResponseEntity<>(service.createUser(dto.getEmail(), dto.getName(), dto.getPassword()), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(service.getUserAll());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(service.getUserByUserId(userId));
    }
}
