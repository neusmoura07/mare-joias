package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutResponseDTO;
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
            CheckoutResponseDTO response = checkoutService.processCheckout(principal.getName(), requestDto);

            // Retorna HTTP 201 (Created) com o JSON contendo o ID do pedido e a URL do Mercado Pago
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            // Captura o erro de falta de estoque e devolve 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}