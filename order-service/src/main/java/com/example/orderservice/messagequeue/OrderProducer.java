package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<Field> fields = Arrays.asList(
            new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "quantity"),
            new Field("int32", true, "unit_price"),
            new Field("int32", true, "total_price")
    );

    private final Schema schema = Schema.builder()
                                        .type("struct")
                                        .fields(fields)
                                        .optional(false)
                                        .name("orders")
                                        .build();

    public OrderResponse send(String topic, String userId, String productId, Integer quantity, Integer unitPrice) {
        String orderId = UUID.randomUUID().toString();
        Payload payload = Payload.builder()
                                 .order_id(orderId)
                                 .user_id(userId)
                                 .product_id(productId)
                                 .quantity(quantity)
                                 .unit_price(unitPrice)
                                 .total_price(quantity * unitPrice)
                                 .build();

        KafkaOrderDTO kafkaOrderDto = new KafkaOrderDTO(schema, payload);

        try {
            String jsonInString = mapper.writeValueAsString(kafkaOrderDto);
            kafkaTemplate.send(topic, jsonInString);
            log.info("Order Producer sent data from the Order microservice: " + kafkaOrderDto);
        } catch(JsonProcessingException e) {
            log.error(e.getMessage());
        }

        return new OrderResponse(productId, quantity, unitPrice, quantity * unitPrice, null, orderId);
    }
}
