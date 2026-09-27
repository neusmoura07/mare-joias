package br.com.marejoias.checkout.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CheckoutRequestDTO(
        @NotNull(message = "O ID do endereço é obrigatório")
        UUID addressId,

        @NotEmpty(message = "O carrinho não pode estar vazio")
        List<CheckoutItemDTO> items
) {}