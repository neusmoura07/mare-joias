package br.com.marejoias.checkout.repository;

import br.com.marejoias.checkout.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // O JpaRepository já nos dá o save(), findById(), etc. por padrão.
}