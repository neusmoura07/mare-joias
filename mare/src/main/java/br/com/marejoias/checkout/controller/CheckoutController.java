package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.service.CheckoutService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    /**
     * Endpoint responsável por processar o fechamento de um pedido.
     */
    @PostMapping
    public ResponseEntity<Order> processCheckout(@RequestBody @Valid CheckoutRequestDto requestDto) {
        Order novoPedido = checkoutService.processCheckout(
                requestDto.items(),
                requestDto.shippingFeeCents(),
                requestDto.shippingAddress()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    /**
     * DTO de transferência de dados para a requisição de checkout.
     */
    public record CheckoutRequestDto(
            @NotEmpty(message = "O carrinho não pode estar vazio")
            List<CheckoutService.ItemRequest> items,

            @NotNull(message = "A taxa de frete é obrigatória")
            Integer shippingFeeCents,

            @NotEmpty(message = "O endereço de entrega é obrigatório")
            String shippingAddress
    ) {}
}