package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.controller.dto.OrderHistoryDTO;
import br.com.marejoias.checkout.controller.dto.OrderStatusUpdateDTO;
import br.com.marejoias.checkout.service.OrderQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    /**
     * Histórico de pedidos do próprio cliente logado (área "Minha Conta").
     */
    @GetMapping("/api/v1/orders/my-orders")
    public ResponseEntity<List<OrderHistoryDTO>> getMyOrders(Principal principal) {
        // principal.getName() contém o e-mail do JWT (configurado na autenticação)
        return ResponseEntity.ok(orderQueryService.getMyOrders(principal.getName()));
    }

    /**
     * Atualização do status de envio de um pedido, restrita a Administradores.
     */
    @PatchMapping("/api/v1/admin/orders/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderStatusUpdateDTO dto) {
        orderQueryService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }
}
