package br.com.marejoias.checkout.domain.entity;

import br.com.marejoias.catalog.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Mantemos a referência ao produto original caso precisemos da foto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Estes dois campos blindam o histórico do cliente contra mudanças futuras de preço/nome
    @Column(name = "product_name_snapshot", nullable = false, length = 100)
    private String productNameSnapshot;

    @Column(name = "unit_price_cents_snapshot", nullable = false)
    private Integer unitPriceCentsSnapshot;

    @Column(length = 10)
    private String size;

    @Column(nullable = false)
    private Integer quantity;
}