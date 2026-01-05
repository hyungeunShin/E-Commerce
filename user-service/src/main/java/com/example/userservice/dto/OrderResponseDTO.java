package com.example.userservice.dto;

import java.util.Date;

public record OrderResponseDTO(Long catalogId, Integer quantity, Integer unitPrice,
                               Integer totalPrice, Date createdAt) {

}