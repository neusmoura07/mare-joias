package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<?> processCheckout(Principal principal, @RequestBody @Valid CheckoutRequestDTO requestDto) {
        try {
            // principal.getName() contém o e-mail do JWT (configurado na autenticação)
            Order novoPedido = checkoutService.processCheckout(principal.getName(), requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);

        } catch (IllegalStateException e) {
            // Captura o erro de falta de estoque e devolve 422 com a mensagem
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}