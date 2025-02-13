package com.example.userservice.security;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService service;
    private final Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService service, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.service = service;
        this.env = env;
    }

    /*
    로그인 시도
    attemptAuthentication
    UserDetailService 의 loadUserByUsername
    성공하면 successfulAuthentication
    */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest login = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword(), new ArrayList<>())
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    간단하게 토큰만 발행해서 Response에 포함 시키는 경우라면 successfulAuthentication를 구현하는게 더 간단
    로그인에 대한 성공, 실패 및 사용자 정의 프로세스가 필요한 경우라면 AuthenticationSuccessHandler를 구현하는 게 좋을 듯
    */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserResponse user = service.getUserByEmail(username);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(env.getProperty("token.secret")));
        String token = Jwts.builder()
                           .subject(user.userId())
                           .expiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                           .signWith(secretKey)
                           .compact();

        response.addHeader("token", token);
        response.addHeader("userId", user.userId());
    }
}
