package br.com.marejoias.checkout.repository;

import br.com.marejoias.checkout.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    // Itens (com o snapshot de nome/preço) de um pedido específico
    List<OrderItem> findByOrderId(UUID orderId);
}