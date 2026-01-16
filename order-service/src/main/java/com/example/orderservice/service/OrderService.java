package com.example.orderservice.service;

import com.example.orderservice.dto.CatalogMessageDTO;
import com.example.orderservice.dto.KafkaSendEventDTO;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<OrderResponseDTO> findByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream().map(OrderResponseDTO::from).toList();
    }

    @Transactional
    public OrderResponseDTO save(String userId, OrderRequestDTO order) {
        OrderEntity saved = orderRepository.save(OrderEntity.builder()
                                           .catalogId(order.catalogId())
                                           .quantity(order.quantity())
                                           .unitPrice(order.unitPrice())
                                           .userId(userId)
                                           .build());
        eventPublisher.publishEvent(
                new KafkaSendEventDTO("decrease-stock", new CatalogMessageDTO(order.catalogId(), order.quantity()))
        );
        return OrderResponseDTO.from(saved);
    }
}
