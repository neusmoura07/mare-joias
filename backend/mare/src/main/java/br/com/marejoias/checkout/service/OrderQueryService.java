package br.com.marejoias.checkout.service;

import br.com.marejoias.checkout.controller.dto.OrderHistoryDTO;
import br.com.marejoias.checkout.controller.dto.OrderItemHistoryDTO;
import br.com.marejoias.checkout.controller.dto.OrderStatusUpdateDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderItem;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    /**
     * Histórico de pedidos do cliente logado, do mais recente para o mais antigo,
     * cada um já com o cabeçalho do pedido e a lista de itens (snapshot).
     */
    public List<OrderHistoryDTO> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toHistoryDTO)
                .toList();
    }

    public void updateOrderStatus(UUID orderId, OrderStatusUpdateDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(dto.status());
        orderRepository.save(order);
    }

    private OrderHistoryDTO toHistoryDTO(Order order) {
        List<OrderItemHistoryDTO> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(this::toItemDTO)
                .toList();

        return new OrderHistoryDTO(
                order.getId(),
                order.getStatus(),
                order.getTotalAmountCents(),
                order.getShippingFeeCents(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemHistoryDTO toItemDTO(OrderItem item) {
        return new OrderItemHistoryDTO(
                item.getId(),
                item.getProductNameSnapshot(),
                item.getUnitPriceCentsSnapshot(),
                item.getSize(),
                item.getQuantity()
        );
    }
}
