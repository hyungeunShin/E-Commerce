package com.example.orderservice.message;

import com.example.orderservice.dto.KafkaSendEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSendEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void eventHandler(KafkaSendEventDTO dto) {
        log.info("KafkaSendEventListener: {}", dto);
        kafkaTemplate.send(dto.topic(), dto.contents());
    }
}
