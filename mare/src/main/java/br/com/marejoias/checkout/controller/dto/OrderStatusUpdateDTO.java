package br.com.marejoias.checkout.controller.dto;

import br.com.marejoias.checkout.domain.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(
        @NotNull(message = "O novo status é obrigatório")
        OrderStatus status
) {}
