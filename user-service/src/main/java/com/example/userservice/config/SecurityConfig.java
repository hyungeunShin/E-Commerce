package com.example.userservice.config;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final Environment env;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers(PathRequest.toH2Console())
                                               .requestMatchers("/actuator/**")
                                               .requestMatchers("/health-check/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults());

        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/**").access(
                        /*
                        new WebExpressionAuthorizationManager(
                                "hasIpAddress('127.0.0.1') or hasIpAddress('::1') or hasIpAddress('192.168.0.28')"
                        )
                        */

                        (authentication, context) -> {
                            boolean m1 = new IpAddressMatcher("127.0.0.1").matches(context.getRequest());
                            boolean m2 = new IpAddressMatcher("::1").matches(context.getRequest());
                            boolean m3 = new IpAddressMatcher("192.168.0.28").matches(context.getRequest());

                            log.info("@@@@@@@@@@@@@@@: {}, {}, {}", m1, m2, m3);

                            return new AuthorizationDecision(m1 || m2 || m3);
                        }
                )
                .anyRequest().authenticated()
        );

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager);

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, env, userService);
        authenticationFilter.setAuthenticationManager(authenticationManager);

        http.addFilter(authenticationFilter);

        return http.build();
    }
}
