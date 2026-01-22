package com.example.userservice.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull(message = "아이디 필수")
        String userId,

        @NotNull(message = "패스워드 필수")
        String password) {

}
