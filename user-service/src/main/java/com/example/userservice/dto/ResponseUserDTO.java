package com.example.userservice.dto;

import com.example.userservice.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserDTO {
    private String email;
    private String name;

    public static ResponseUserDTO from(UserEntity user) {
        return new ResponseUserDTO(user.getEmail(), user.getName());
    }
}
