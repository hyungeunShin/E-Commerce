package com.example.userservice.event;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Init {
    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        userService.save(new UserRequestDTO("aaa", "홍길동", "123"));
        userService.save(new UserRequestDTO("bbb", "박길동", "123"));
        userService.save(new UserRequestDTO("ccc", "이길동", "123"));
    }
}
