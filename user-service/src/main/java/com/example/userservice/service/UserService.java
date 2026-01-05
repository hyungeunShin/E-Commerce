package com.example.userservice.service;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(UserResponseDTO::from).toList();
    }

    public UserResponseDTO findByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return UserResponseDTO.from(userEntity, new ArrayList<>());
    }

    @Transactional
    public UserResponseDTO save(UserRequestDTO user) {
        return UserResponseDTO.from(userRepository.save(UserEntity.builder()
                                                                  .userId(user.userId())
                                                                  .name(user.name())
                                                                  .password(passwordEncoder.encode(user.password()))
                                                                  .build()));
    }
}
