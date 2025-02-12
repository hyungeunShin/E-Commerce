package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NotEmpty(message = "이메일 공백 X")
    @Email
    private String email;

    @NotBlank(message = "비밀번호 공백 X")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상")
    private String password;
}
