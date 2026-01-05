package com.example.orderservice.dto;

public record OrderRequestDTO(Long catalogId, Integer quantity, Integer unitPrice) {

}
