package com.example.catalogservice.dto;

import com.example.catalogservice.entity.Catalog;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CatalogResponse(
        String productId, String productName, Integer unitPrice,
        Integer stock, Date createdAt) {
    public static CatalogResponse from(Catalog catalog) {
        return new CatalogResponse(catalog.getProductId(), catalog.getProductName(), catalog.getUnitPrice(),
                catalog.getStock(), catalog.getCreatedAt());
    }
}
