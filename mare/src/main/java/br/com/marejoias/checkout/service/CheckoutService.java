package br.com.marejoias.checkout.service;

import br.com.marejoias.catalog.domain.entity.ProductSize;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderItem;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductSizeRepository productSizeRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public Order processCheckout(String userEmail, CheckoutRequestDTO request) {

        // 1. Identificar o cliente logado e o endereço de entrega
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));

        List<OrderItem> itemsToSave = new ArrayList<>();
        List<ProductSize> sizesToUpdate = new ArrayList<>();
        int totalGeralCents = 0;

        // 2. Loop de Validação (NADA é salvo na base de dados ainda)
        for (var itemDto : request.items()) {
            ProductSize productSize = productSizeRepository.findById(itemDto.sizeId())
                    .orElseThrow(() -> new RuntimeException("Tamanho de produto não encontrado."));

            // Validação de Estoque
            if (productSize.getStockQuantity() < itemDto.quantity()) {
                throw new IllegalStateException("Estoque insuficiente para o produto " + productSize.getProduct().getName());
            }

            // Subtrai o estoque em memória e guarda na lista para salvar depois
            productSize.setStockQuantity(productSize.getStockQuantity() - itemDto.quantity());
            sizesToUpdate.add(productSize);

            // Prepara o Snapshot (ainda sem associar ao Order, pois ele não foi salvo)
            OrderItem orderItem = OrderItem.builder()
                    .product(productSize.getProduct())
                    .size(productSize.getSizeName())
                    .quantity(itemDto.quantity())
                    .unitPriceCentsSnapshot(productSize.getProduct().getPriceCents())
                    .productNameSnapshot(productSize.getProduct().getName())
                    .build();

            itemsToSave.add(orderItem);
            totalGeralCents += (orderItem.getUnitPriceCentsSnapshot() * orderItem.getQuantity());
        }

        // 3. Se passou por todas as validações sem lançar exceção, agora SIM salvamos tudo!

        // 3.1 Salvar os estoques atualizados
        productSizeRepository.saveAll(sizesToUpdate);

        // 3.2 Criar e salvar o Pedido (Cabeçalho)
        Order order = Order.builder()
                .user(user)
                .status("PENDING")
                .totalAmountCents(totalGeralCents)
                .shippingFeeCents(0)
                .shippingAddress(address.getStreet())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 3.3 Associar o Pedido salvo a cada Item e salvar os Itens
        for (OrderItem item : itemsToSave) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(itemsToSave);

        return savedOrder;
    }
}