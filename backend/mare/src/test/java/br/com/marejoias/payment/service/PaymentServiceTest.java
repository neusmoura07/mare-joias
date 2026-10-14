package br.com.marejoias.payment.service;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.payment.controller.dto.PaymentResult;
import br.com.marejoias.payment.controller.dto.PaymentWebhookDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldProcessPaymentSuccessfullyWhenStatusIsSuccess() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();
        PaymentWebhookDTO dto = new PaymentWebhookDTO(orderId, PaymentResult.SUCCESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        paymentService.processPayment(dto);

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldCancelOrderWhenPaymentStatusIsFailed() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).build();
        PaymentWebhookDTO dto = new PaymentWebhookDTO(orderId, PaymentResult.FAILED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        paymentService.processPayment(dto);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderIsAlreadyPaid() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).status(OrderStatus.PAID).build();
        PaymentWebhookDTO dto = new PaymentWebhookDTO(orderId, PaymentResult.SUCCESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(dto);
        });

        assertEquals("Este pedido já foi pago anteriormente", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        PaymentWebhookDTO dto = new PaymentWebhookDTO(orderId, PaymentResult.SUCCESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(dto);
        });

        assertEquals("Pedido não encontrado", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}