package com.example.userservice.service;

import com.example.userservice.dto.OrderResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(String email, String name, String password) {
        if(repository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일");
        }
        User user = new User(email, name, UUID.randomUUID().toString(), passwordEncoder.encode(password));
        repository.save(user);
        return UserResponse.from(user);
    }

    public UserResponse getUserByUserId(String userId) {
        User user = repository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        List<OrderResponse> orders = new ArrayList<>();
        return UserResponse.from(user, orders);
    }

    public List<UserResponse> getUserAll() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }
}
