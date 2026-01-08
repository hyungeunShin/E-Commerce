package com.example.userservice.config.openfeign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class OpenFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest incoming = attributes.getRequest();
        String authorization = incoming.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("OpenFeignInterceptor.authorization: {}", authorization);
        requestTemplate.header("Authorization", authorization);
    }
}
