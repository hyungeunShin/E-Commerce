package com.example.userservice.security;

import com.example.userservice.service.UserDetailService;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final Environment env;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                           .requestMatchers("/h2-console/**", "/welcome", "/health_check", "/error/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        IpAddressMatcher ipv4Matcher = new IpAddressMatcher("127.0.0.1");
        IpAddressMatcher ipv6Matcher = new IpAddressMatcher("::1");
        http.authorizeHttpRequests(authorizeRequests ->
//                authorizeRequests.requestMatchers("/user-service/**").permitAll()

                authorizeRequests
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/**").access((authentication, context) -> {
                            log.info("@@@@@@@@@@@@@@@@ : {}", ipv4Matcher.matches(context.getRequest()));
                            log.info("@@@@@@@@@@@@@@@@ : {}", ipv6Matcher.matches(context.getRequest()));
                            boolean isAllowed = ipv4Matcher.matches(context.getRequest()) || ipv6Matcher.matches(context.getRequest());
                            return new AuthorizationDecision(isAllowed);
                        })
        );

        http.authenticationManager(authenticationManager(http));
        http.addFilter(authenticationFilter(http));
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(HttpSecurity http) throws Exception {
        return new AuthenticationFilter(authenticationManager(http), userService, env);
    }
}
