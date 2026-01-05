package com.example.catalogservice.service;

import com.example.catalogservice.dto.CatalogResponseDTO;
import com.example.catalogservice.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {
    private final CatalogRepository catalogRepository;

    public List<CatalogResponseDTO> findAll() {
        return catalogRepository.findAll().stream().map(CatalogResponseDTO::from).toList();
    }
}
