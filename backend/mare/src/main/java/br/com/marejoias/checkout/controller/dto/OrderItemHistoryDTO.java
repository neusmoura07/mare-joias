package br.com.marejoias.checkout.controller.dto;

import java.util.UUID;

public record OrderItemHistoryDTO(
        UUID id,
        String productNameSnapshot,
        Integer unitPriceCentsSnapshot,
        String size,
        Integer quantity
) {}
