package br.com.marejoias.catalog.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ProductUpdateDTO(
        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        String description,

        String material,

        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        Integer priceCents,

        String imageUrl
) {}
