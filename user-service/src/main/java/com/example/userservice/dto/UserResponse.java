package com.example.userservice.dto;

import com.example.userservice.entity.User;

public record UserResponse(Long id, String email, String name, String userId) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getUserId());
    }
}
