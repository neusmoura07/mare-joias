package br.com.marejoias.checkout.service;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Processa a criação de um pedido validando estoque e calculando o total seguro em centavos.
     */
    @Transactional
    public Order processCheckout(List<ItemRequest> itensDoCarrinho, Integer taxaFreteCents, String enderecoEntrega) {
        
        int subtotalCents = 0;

        // 1. Validação de Estoque e Cálculo de Preços (Snapshot)
        for (ItemRequest itemDto : itensDoCarrinho) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado no catálogo."));

            if (product.getStockQuantity() < itemDto.quantity()) {
                throw new RuntimeException("Estoque insuficiente para o produto selecionado: " + product.getName());
            }

            subtotalCents += product.getPriceCents() * itemDto.quantity();
        }

        int totalGeralCents = subtotalCents + taxaFreteCents;

        // 2. Criação da Entidade Order (Cabeçalho do Pedido)
        Order order = Order.builder()
                .status("PENDING_PAYMENT")
                .totalAmountCents(totalGeralCents)
                .shippingFeeCents(taxaFreteCents)
                .shippingAddress(enderecoEntrega)
                .build();

        // Salva o pedido para gerar o ID de relacionamento
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    // Record auxiliar para receber os dados do item vindo da API/Testes
    public record ItemRequest(java.util.UUID productId, Integer quantity) {}
}