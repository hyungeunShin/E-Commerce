package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in Order Service, port(local.server.port)=%s, port(server.port)=%s".formatted(env.getProperty("local.server.port"), env.getProperty("server.port"));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getOrder(@PathVariable("userId") String userId) {
        log.info("Called getOrder");
        return ResponseEntity.status(HttpStatus.OK)
                             .body(orderService.findByUserId(userId));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable("userId") String userId, @RequestBody OrderRequestDTO order) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(orderService.save(userId, order));
    }
}