package br.com.marejoias.checkout.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutItemDTO(
        @NotNull(message = "O ID do tamanho é obrigatório")
        UUID sizeId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantity
) {}