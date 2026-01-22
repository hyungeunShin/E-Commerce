package com.example.secondservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/second-service")
@RequiredArgsConstructor
public class SecondServiceController {
    private final Environment env;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome Second service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("s-request") String header) {
        log.info(header);
        return "Message From Second Service";
    }

    @GetMapping("/check")
    public String check() {
        return String.format("Check Second Service on PORT %s", env.getProperty("local.server.port"));
    }
}
