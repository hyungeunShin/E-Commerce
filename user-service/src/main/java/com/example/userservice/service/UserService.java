package com.example.userservice.service;

import com.example.userservice.dto.RequestUserDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity createUser(RequestUserDTO user) {
        return userRepository.save(UserEntity.builder()
                                             .email(user.getEmail())
                                             .name(user.getName())
                                             .password(passwordEncoder.encode(user.getPassword()))
                                             .build());
    }
}
