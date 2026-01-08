package com.example.userservice.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final Environment env;
    private final UserDetailsService userDetailsService;

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers(PathRequest.toH2Console())
                                               .requestMatchers("/actuator/**")
                                               .requestMatchers("/health-check/**")
                                               .requestMatchers("/error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(HttpMethod.POST, "/login", "/users").permitAll()
//                    .requestMatchers("/**").access(
//                        /*
//                        new WebExpressionAuthorizationManager(
//                                "hasIpAddress('127.0.0.1') or hasIpAddress('::1') or hasIpAddress('192.168.0.28')"
//                        )
//                        */
//
//                        (authentication, context) -> {
//                            boolean m1 = new IpAddressMatcher("127.0.0.1").matches(context.getRequest());
//                            boolean m2 = new IpAddressMatcher("::1").matches(context.getRequest());
//                            boolean m3 = new IpAddressMatcher("192.168.0.28").matches(context.getRequest());
//
//                            log.info("@@@@@@@@@@@@@@@: {}, {}, {}", m1, m2, m3);
//
//                            return new AuthorizationDecision(m1 || m2 || m3);
//                        }
//                    )
                    .anyRequest().authenticated()
        );

//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//
//        http.authenticationManager(authenticationManager);
//
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager);
//
//        http.addFilter(authenticationFilter);

        http.addFilterBefore(new JwtAuthenticationFilter(env), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
