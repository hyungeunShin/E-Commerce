package com.example.userservice.dto;

import com.example.userservice.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDTO(String userId, String name, List<OrderResponseDTO> orders) {
    public static UserResponseDTO from(UserEntity user) {
        return new UserResponseDTO(user.getUserId(), user.getName(), null);
    }

    public static UserResponseDTO from(UserEntity user, List<OrderResponseDTO> orders) {
        return new UserResponseDTO(user.getUserId(), user.getName(), orders);
    }
}
