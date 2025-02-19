package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.KafkaDecreaseStockDTO;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.messagequeue.CatalogProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;
    private final CatalogProducer catalogProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return "It's Working in User Service on PORT %s".formatted(request.getServerPort());
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody CreateOrderRequest dto) {
//        OrderResponse order = service.createOrder(dto.productId(), dto.quantity(), dto.unitPrice(), userId);
        catalogProducer.send("example-catalog-topic", new KafkaDecreaseStockDTO(dto.productId(), dto.quantity()));

        /*
        {
            "name": "my-order-sink-connect",
            "config": {
                "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                "connection.url": "jdbc:mysql://mysql:3306/mydb",
                "connection.user": "root",
                "connection.password": "1234",
                "auto.create": "true",
                "auto.evolve": "true",
                "delete.enabled": "false",
                "tasks.max": "1",
                "topics": "orders"
            }
        }
        */
        OrderResponse order = orderProducer.send("orders", userId, dto.productId(), dto.quantity(), dto.unitPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrder(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getOrdersByUserId(userId));
    }
}
