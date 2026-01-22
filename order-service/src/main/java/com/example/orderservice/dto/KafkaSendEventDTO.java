package com.example.orderservice.dto;

public record KafkaSendEventDTO(String topic, Object contents) {
}
