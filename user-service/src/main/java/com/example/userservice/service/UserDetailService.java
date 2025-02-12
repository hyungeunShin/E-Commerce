package com.example.userservice.service;

import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.userservice.entity.User user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new User(
                user.getEmail(), user.getEncryptedPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }
}
