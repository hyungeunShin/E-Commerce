package com.example.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotNull(message = "아이디 필수")
        @Size(min = 2, message = "아이디 최소 2글자 이상")
        String userId,

        @NotNull(message = "이름 필수")
        @Size(min = 2, message = "이름 최소 2글자 이상")
        String name,

        @NotNull(message = "패스워드 필수")
        @Size(min = 8, message = "패스워드 최소 8글자 이상")
        String password
) {}
