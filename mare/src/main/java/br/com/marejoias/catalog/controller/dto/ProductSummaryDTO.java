package br.com.marejoias.catalog.controller.dto;

import br.com.marejoias.catalog.domain.entity.Product;

import java.util.UUID;

public record ProductSummaryDTO(
        UUID id,
        String sku,
        String name,
        String slug,
        String description,
        String material,
        Integer priceCents,
        String imageUrl,
        Integer stockQuantity,
        Boolean isActive,
        String categoryName,
        String categorySlug
) {
    public static ProductSummaryDTO from(Product product) {
        var category = product.getCategory();
        return new ProductSummaryDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getMaterial(),
                product.getPriceCents(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getIsActive(),
                category != null ? category.getName() : null,
                category != null ? category.getSlug() : null
        );
    }
}
