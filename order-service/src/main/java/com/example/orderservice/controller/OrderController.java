package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return "It's Working in User Service on PORT %s".formatted(request.getServerPort());
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody CreateOrderRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(dto.productId(), dto.quantity(), dto.unitPrice(), userId));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrder(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getOrdersByUserId(userId));
    }
}
