package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponseDTO(Long catalogId, Integer quantity, Integer unitPrice,
                               Integer totalPrice, Date createdAt) {
    public static OrderResponseDTO from(OrderEntity order) {
        return new OrderResponseDTO(
                order.getCatalogId(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}