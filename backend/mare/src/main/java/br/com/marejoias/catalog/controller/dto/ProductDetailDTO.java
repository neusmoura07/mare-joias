package br.com.marejoias.catalog.controller.dto;

import java.util.List;
import java.util.UUID;

public record ProductDetailDTO(
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
        UUID categoryId,       // <-- O NOVO CAMPO (com o tipo UUID correto)
        String categoryName,
        String categorySlug,
        List<ProductSizeDTO> sizes
) {}