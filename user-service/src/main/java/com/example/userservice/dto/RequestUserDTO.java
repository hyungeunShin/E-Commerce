package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserDTO {
    @NotNull(message = "이메일 필수")
    @Size(min = 2, message = "이메일 최소 2글자 이상")
    @Email
    private String email;

    @NotNull(message = "이름 필수")
    @Size(min = 2, message = "이름 최소 2글자 이상")
    private String name;

    @NotNull(message = "패스워드 필수")
    @Size(min = 8, message = "패스워드 최소 8글자 이상")
    private String password;
}
