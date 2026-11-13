package br.com.marejoias.checkout.controller.dto;

import java.util.UUID;

public record CheckoutResponseDTO(
        UUID orderId,
        String paymentUrl
) {}