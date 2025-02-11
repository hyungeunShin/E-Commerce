package com.example.orderservice.dto;

public record CreateOrderRequest(String productId, Integer quantity, Integer unitPrice) {
}
