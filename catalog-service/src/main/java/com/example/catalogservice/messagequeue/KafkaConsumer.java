package com.example.catalogservice.messagequeue;

import com.example.catalogservice.dto.KafkaDecreaseStockDTO;
import com.example.catalogservice.entity.Catalog;
import com.example.catalogservice.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CatalogRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "example-catalog-topic")
    public void updateQuantity(String kafkaMessage) {
        log.info("Kafka Message: ->" + kafkaMessage);

        try {
            KafkaDecreaseStockDTO dto = mapper.readValue(kafkaMessage, KafkaDecreaseStockDTO.class);
            Optional<Catalog> entity = repository.findByProductId(dto.productId());

            if(entity.isPresent()) {
                Catalog catalog = entity.get();
                catalog.decreaseStock(dto.quantity());
                repository.save(catalog);
            }
        } catch(JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
