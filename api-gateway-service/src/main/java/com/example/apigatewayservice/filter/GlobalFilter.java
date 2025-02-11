package com.example.apigatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage : {}", config.getBaseMessage());

            if(config.isPreLogger()) {
                log.info("Global Filter Start : {}", request.getId());
            }

            return chain.filter(exchange)
                    .onErrorResume(throwable -> {
                        log.error("Exception 발생: {}", throwable.getMessage(), throwable);

                        String errorMessage = "잘못된 요청: " + throwable.getMessage();
                        byte[] bytes = errorMessage.getBytes();

                        response.setStatusCode(HttpStatus.BAD_REQUEST);

                        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                    })
                    .then(Mono.fromRunnable(() -> {
                        if(config.isPostLogger()) {
                            log.info("Global Filter End : {}", response.getStatusCode());
                        }
                    }));
        };
    }

    @Getter
    @Setter
    public static class Config {
        //Put the Configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
