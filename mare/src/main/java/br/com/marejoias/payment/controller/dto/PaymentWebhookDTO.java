package br.com.marejoias.payment.controller.dto;

import java.util.UUID;

public record PaymentWebhookDTO(
        UUID orderId,
        PaymentResult paymentStatus
) {}