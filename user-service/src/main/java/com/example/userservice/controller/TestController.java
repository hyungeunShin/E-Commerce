package com.example.userservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class TestController {
    @GetMapping("/a")
    public String a(@RequestHeader(name = "user-request") String header) {
        log.info(header);
        return "a";
    }

    @GetMapping("/b")
    public String b(@RequestHeader(name = "user-request") String header) {
        log.info(header);
        return "b";
    }

    @GetMapping("/c")
    public String c(HttpServletRequest request) {
        log.info("Server port : {}", request.getServerPort());
        return "c";
    }
}
