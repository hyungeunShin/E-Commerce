package com.example.catalogservice.dto;

import com.example.catalogservice.entity.CatalogEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CatalogResponseDTO(String productName, Integer unitPrice,
                                 Integer stock, Date createdAt) {
    public static CatalogResponseDTO from(CatalogEntity catalog) {
        return new CatalogResponseDTO(
                catalog.getProductName(),
                catalog.getUnitPrice(),
                catalog.getStock(),
                catalog.getCreatedAt()
        );
    }
}
