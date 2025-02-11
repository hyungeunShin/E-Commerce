package com.example.orderservice.dto;

import com.example.orderservice.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        String productId, Integer quantity, Integer unitPrice,
        Integer totalPrice, Date createdAt, String orderId) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getProductId(), order.getQuantity(), order.getUnitPrice(),
                order.getTotalPrice(), order.getCreatedAt(), order.getOrderId());
    }
}
