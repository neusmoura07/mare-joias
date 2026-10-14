package br.com.marejoias.payment.controller;

import br.com.marejoias.payment.controller.dto.PaymentWebhookDTO;
import br.com.marejoias.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody PaymentWebhookDTO webhookDTO) {
        try {
            paymentService.processPayment(webhookDTO);
            return ResponseEntity.ok().build(); // HTTP 200 OK
            
        } catch (IllegalArgumentException e) {
            // Pedido não existe (HTTP 404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
                    
        } catch (IllegalStateException e) {
            // Pagamento duplo (HTTP 422)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}