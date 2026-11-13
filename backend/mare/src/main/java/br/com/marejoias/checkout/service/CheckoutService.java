package br.com.marejoias.checkout.service;

import br.com.marejoias.catalog.domain.entity.ProductSize;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutResponseDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderItem;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.payment.service.MercadoPagoPaymentService;
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

    // Injetamos o nosso novo serviço especialista
    private final MercadoPagoPaymentService paymentService;

    @Transactional
    public CheckoutResponseDTO processCheckout(String userEmail, CheckoutRequestDTO request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));

        List<OrderItem> itemsToSave = new ArrayList<>();
        List<ProductSize> sizesToUpdate = new ArrayList<>();
        int totalGeralCents = 0;

        for (var itemDto : request.items()) {
            ProductSize productSize = productSizeRepository.findById(itemDto.sizeId())
                    .orElseThrow(() -> new RuntimeException("Tamanho de produto não encontrado."));

            if (productSize.getStockQuantity() < itemDto.quantity()) {
                throw new IllegalStateException("Estoque insuficiente para o produto " + productSize.getProduct().getName());
            }

            productSize.setStockQuantity(productSize.getStockQuantity() - itemDto.quantity());
            sizesToUpdate.add(productSize);

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

        productSizeRepository.saveAll(sizesToUpdate);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmountCents(totalGeralCents)
                .shippingFeeCents(0)
                .shippingAddress(address.getStreet())
                .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : itemsToSave) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(itemsToSave);

        String paymentUrl = paymentService.createPaymentPreference(savedOrder, itemsToSave);

        return new CheckoutResponseDTO(savedOrder.getId(), paymentUrl);
    }
}