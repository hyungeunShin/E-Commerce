package com.example.userservice.dto;

import com.example.userservice.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(Long id, String email, String name, String userId, List<OrderResponse> orders) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getUserId(), null);
    }

    public static UserResponse from(User user, List<OrderResponse> orders) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getUserId(), orders);
    }
}
