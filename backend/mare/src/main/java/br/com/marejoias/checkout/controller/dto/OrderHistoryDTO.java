package br.com.marejoias.checkout.controller.dto;

import br.com.marejoias.checkout.domain.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderHistoryDTO(
        UUID id,
        OrderStatus status,
        Integer totalAmountCents,
        Integer shippingFeeCents,
        String shippingAddress,
        LocalDateTime createdAt,
        List<OrderItemHistoryDTO> items
) {}
