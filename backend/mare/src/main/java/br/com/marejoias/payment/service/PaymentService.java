package br.com.marejoias.payment.service;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus; // Importe o Enum do Checkout
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.payment.controller.dto.PaymentResult; // Importe o Enum do Pagamento
import br.com.marejoias.payment.controller.dto.PaymentWebhookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    @Transactional
    public void processPayment(PaymentWebhookDTO dto) {
        // 1. Busca o pedido (Se não achar, lança exceção para o Controller dar 404)
        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        // 2. Trava de segurança contra pagamento duplo
        if (OrderStatus.PAID.equals(order.getStatus())) {
            throw new IllegalStateException("Este pedido já foi pago anteriormente");
        }

        // 3. Atualiza o status conforme o webhook
        if (PaymentResult.SUCCESS.equals(dto.paymentStatus())) {
            order.setStatus(OrderStatus.PAID);
        } else if (PaymentResult.FAILED.equals(dto.paymentStatus())) {
            order.setStatus(OrderStatus.CANCELLED);
            // No futuro, aqui chamaremos um método para devolver o estoque dos itens
        }

        orderRepository.save(order);
    }
}