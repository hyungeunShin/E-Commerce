package com.example.userservice.config.restclient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Slf4j
public class RestClientInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
//        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        log.info("RestClientInterceptor.token: {}", auth.getToken());
//        request.getHeaders().add(HttpHeaders.AUTHORIZATION, auth.getToken());
//        return execution.execute(request, body);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest incoming = attributes.getRequest();
        String authorization = incoming.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("RestClientInterceptor.authorization: {}", authorization);
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, authorization);
        return execution.execute(request, body);
    }
}
