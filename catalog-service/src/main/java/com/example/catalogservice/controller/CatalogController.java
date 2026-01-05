package com.example.catalogservice.controller;

import com.example.catalogservice.dto.CatalogResponseDTO;
import com.example.catalogservice.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {
    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in Catalog Service, port(local.server.port)=%s, port(server.port)=%s".formatted(env.getProperty("local.server.port"), env.getProperty("server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogResponseDTO>> getCatalogs() {
        return ResponseEntity.status(HttpStatus.OK).body(catalogService.findAll());
    }
}