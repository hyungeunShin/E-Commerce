package com.example.userservice.dto;

import java.util.Date;

public record OrderResponse(
        String productId, Integer quantity, Integer unitPrice,
        Integer totalPrice, Date createdAt, String orderId) {

}
