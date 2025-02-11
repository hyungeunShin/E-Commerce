package com.example.orderservice.service;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    @Transactional
    public OrderResponse createOrder(String productId, Integer quantity, Integer unitPrice, String userId) {
        Order order = new Order(productId, quantity, unitPrice, quantity * unitPrice, userId, UUID.randomUUID().toString());
        return OrderResponse.from(repository.save(order));
    }

    public OrderResponse getOrderByOrderId(String orderId) {
        Order order = repository.findByOrderId(orderId).orElseThrow(NullPointerException::new);
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getOrdersByUserId(String userId) {
        return repository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }
}
