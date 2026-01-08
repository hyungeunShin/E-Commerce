package com.example.userservice.client;

import com.example.userservice.config.openfeign.FeignErrorDecoder;
import com.example.userservice.dto.OrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//Eureka Server에서 order-service를 검색하여 직접 호출
//@FeignClient(name = "order-service")
//api-gateway-service를 거쳐 호출
@FeignClient(name = "order-service", url = "http://localhost:8000/order-service", configuration = FeignErrorDecoder.class)
public interface OrderServiceClient {
    @GetMapping("/{userId}/orders")
    List<OrderResponseDTO> getOrder(@PathVariable("userId") String userId);
}
