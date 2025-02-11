package com.example.catalogservice.service;

import com.example.catalogservice.dto.CatalogResponse;
import com.example.catalogservice.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogService {
    private final CatalogRepository repository;

    public List<CatalogResponse> getAllCatalogs() {
        return repository.findAll().stream().map(CatalogResponse::from).toList();
    }
}
