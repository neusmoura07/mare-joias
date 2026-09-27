package br.com.marejoias.catalog.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O slug é obrigatório")
        String slug
) {}
