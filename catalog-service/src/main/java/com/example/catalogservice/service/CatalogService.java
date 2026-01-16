package com.example.catalogservice.service;

import com.example.catalogservice.dto.CatalogMessageDTO;
import com.example.catalogservice.dto.CatalogResponseDTO;
import com.example.catalogservice.entity.CatalogEntity;
import com.example.catalogservice.repository.CatalogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {
    private final CatalogRepository catalogRepository;

    public List<CatalogResponseDTO> findAll() {
        return catalogRepository.findAll().stream().map(CatalogResponseDTO::from).toList();
    }

    @Transactional
    @KafkaListener(topics = "decrease-stock", groupId = "consumerGroup")
    public void decreaseStock(CatalogMessageDTO dto) {
        log.info("KafkaListener: {}", dto);
        CatalogEntity catalog = catalogRepository.findById(dto.id()).orElseThrow(() -> new EntityNotFoundException("없는 상품"));
        catalog.decreaseStock(dto.stock());
        catalogRepository.saveAndFlush(catalog);
    }
}
