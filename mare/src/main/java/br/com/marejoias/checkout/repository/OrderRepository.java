package br.com.marejoias.checkout.repository;

import br.com.marejoias.checkout.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // O JpaRepository já nos dá o save(), findById(), etc. por padrão.

    // Histórico de pedidos do cliente, do mais recente para o mais antigo
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Último pedido criado (usado em testes de BDD para localizar o pedido
    // criado por um step "Dado" genérico compartilhado entre features)
    Order findTopByOrderByCreatedAtDesc();
}