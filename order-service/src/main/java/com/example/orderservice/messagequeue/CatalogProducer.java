package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.KafkaDecreaseStockDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void send(String topic, KafkaDecreaseStockDTO dto) {
        try {
            String jsonInString = mapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, jsonInString);
            log.info("Kafka Producer sent data from the Order microservice: " + dto);
        } catch(JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
