package com.example.userservice.config.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    /*
    RequestContextHolder는 내부적으로 ThreadLocal을 사용하여 현재 요청(Request) 정보를 저장
    ThreadLocal은 이름 그대로 "해당 쓰레드 내에서만 유효한 변수"

    CircuitBreaker의 run()을 보면 executorService로 새로운 스레드에서 시작을 함
    따라서 OpenFeignInterceptor에서 RequestContextHolder가 null이 나옴
    */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilience4JCircuitBreakerFactoryCustomizer() {
//        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
//                .timeoutDuration(Duration.ofSeconds(4))
//                .build();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(2)
                .build();

        return factory -> {
            factory.configureExecutorService(null);
            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//                   .timeLimiterConfig(timeLimiterConfig)
                   .circuitBreakerConfig(circuitBreakerConfig)
                   .build());
        };
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("orderService");
    }
}
