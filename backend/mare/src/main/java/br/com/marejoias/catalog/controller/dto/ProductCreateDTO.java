package br.com.marejoias.catalog.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ProductCreateDTO(
        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,

        @NotBlank(message = "O SKU é obrigatório")
        String sku,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O slug é obrigatório")
        String slug,

        String description,

        String material,

        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        Integer priceCents,

        String imageUrl,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
        Integer stockQuantity
) {}
