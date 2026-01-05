package com.example.userservice.config;

import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Environment env;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env, UserService userService) {
        super(authenticationManager);
        this.env = env;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.userId(),
                            loginRequest.password(),
                            new ArrayList<>()
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        String userName = ((User) authResult.getPrincipal()).getUsername();
        UserResponseDTO dto = userService.findByUserId(userName);

        byte[] secretKeyBytes = Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8);

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                           .subject(dto.userId())
                           .expiration(Date.from(now.plusMillis(Long.parseLong(Objects.requireNonNull(env.getProperty("token.expiration-time"))))))
                           .issuedAt(Date.from(now))
                           .signWith(secretKey)
                           .compact();

        response.addHeader("token", token);
        response.addHeader("userId", dto.userId());
    }
}
