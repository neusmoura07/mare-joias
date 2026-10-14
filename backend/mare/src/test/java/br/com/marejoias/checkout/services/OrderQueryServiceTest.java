package br.com.marejoias.checkout.services;

import br.com.marejoias.checkout.controller.dto.OrderHistoryDTO;
import br.com.marejoias.checkout.controller.dto.OrderStatusUpdateDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderItem;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.checkout.service.OrderQueryService;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderQueryService orderQueryService;

    @Test
    @DisplayName("Deve retornar o histórico de pedidos do cliente, com itens, do mais recente para o mais antigo")
    void shouldReturnOrderHistoryWithItems() {
        UUID userId = UUID.randomUUID();
        User cliente = User.builder().id(userId).email("maria@teste.com").role(Role.CUSTOMER).build();

        Order pedido = Order.builder()
                .id(UUID.randomUUID())
                .user(cliente)
                .status(OrderStatus.PAID)
                .totalAmountCents(10000)
                .shippingFeeCents(0)
                .shippingAddress("Rua A")
                .build();

        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .order(pedido)
                .productNameSnapshot("Anel de Prata")
                .unitPriceCentsSnapshot(10000)
                .size("18")
                .quantity(1)
                .build();

        when(userRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(cliente));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(pedido));
        when(orderItemRepository.findByOrderId(pedido.getId())).thenReturn(List.of(item));

        List<OrderHistoryDTO> result = orderQueryService.getMyOrders("maria@teste.com");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).items().size());
        assertEquals("Anel de Prata", result.get(0).items().get(0).productNameSnapshot());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar histórico de usuário inexistente")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("nao-existe@teste.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderQueryService.getMyOrders("nao-existe@teste.com"));
    }

    @Test
    @DisplayName("Deve atualizar o status de um pedido existente")
    void shouldUpdateOrderStatus() {
        UUID orderId = UUID.randomUUID();
        Order pedido = Order.builder().id(orderId).status(OrderStatus.PAID).build();
        OrderStatusUpdateDTO dto = new OrderStatusUpdateDTO(OrderStatus.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pedido));

        orderQueryService.updateOrderStatus(orderId, dto);

        assertEquals(OrderStatus.SHIPPED, pedido.getStatus());
        verify(orderRepository, times(1)).save(pedido);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status de pedido inexistente")
    void shouldThrowWhenUpdatingNonExistentOrder() {
        UUID orderId = UUID.randomUUID();
        OrderStatusUpdateDTO dto = new OrderStatusUpdateDTO(OrderStatus.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderQueryService.updateOrderStatus(orderId, dto));
        verify(orderRepository, never()).save(any());
    }
}
