package br.com.marejoias.catalog.controller.dto;

import java.util.UUID;

public record ProductSizeDTO(UUID id, String sizeName, Integer stockQuantity) {}
