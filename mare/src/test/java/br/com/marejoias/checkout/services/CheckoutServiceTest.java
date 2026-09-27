package br.com.marejoias.checkout.services;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.domain.entity.ProductSize;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import br.com.marejoias.checkout.controller.dto.CheckoutItemDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.checkout.service.CheckoutService;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.identity.domain.entity.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductSizeRepository productSizeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    @DisplayName("Deve processar o checkout com sucesso quando houver estoque suficiente e calcular o total correto")
    void shouldProcessCheckoutSuccessfullyWhenStockIsSufficient() {
        // 1. Preparação dos IDs e Mocks
        String userEmail = "cliente@teste.com";
        UUID addressId = UUID.randomUUID();
        UUID sizeId = UUID.randomUUID();

        User mockUser = User.builder().id(UUID.randomUUID()).email(userEmail).build();
        Address mockAddress = Address.builder().id(addressId).street("Rua das Joias, 100").build();

        Product mockProduct = Product.builder().name("Anel de Prata").priceCents(15000).build();
        ProductSize mockProductSize = ProductSize.builder().id(sizeId).product(mockProduct).sizeName("18").stockQuantity(10).build();

        CheckoutRequestDTO requestDto = new CheckoutRequestDTO(
                addressId,
                List.of(new CheckoutItemDTO(sizeId, 2)) // 2 unidades x 15000 = 30000
        );

        // 2. Configurando os comportamentos falsos (Mocks)
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(productSizeRepository.findById(sizeId)).thenReturn(Optional.of(mockProductSize));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 3. Ação
        Order result = checkoutService.processCheckout(userEmail, requestDto);

        // 4. Verificações (Asserts)
        assertNotNull(result);
        assertEquals(30000, result.getTotalAmountCents()); // Total calculado sem frete
        assertEquals(OrderStatus.PENDING, result.getStatus());

        // Verifica se o estoque foi deduzido e se os repositórios foram chamados
        assertEquals(8, mockProductSize.getStockQuantity());
        verify(productSizeRepository, times(1)).saveAll(any());
        verify(orderItemRepository, times(1)).saveAll(any());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e impedir o checkout quando o estoque for insuficiente")
    void shouldThrowExceptionWhenStockIsInsufficient() {
        String userEmail = "cliente@teste.com";
        UUID addressId = UUID.randomUUID();
        UUID sizeId = UUID.randomUUID();

        User mockUser = User.builder().id(UUID.randomUUID()).email(userEmail).build();
        Address mockAddress = Address.builder().id(addressId).street("Endereço Teste").build();

        Product mockProduct = Product.builder().name("Brinco de Diamante").priceCents(50000).build();
        // Apenas 1 em estoque
        ProductSize mockProductSize = ProductSize.builder().id(sizeId).product(mockProduct).sizeName("Único").stockQuantity(1).build();

        CheckoutRequestDTO requestDto = new CheckoutRequestDTO(
                addressId,
                List.of(new CheckoutItemDTO(sizeId, 2)) // Tentando comprar 2
        );

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(productSizeRepository.findById(sizeId)).thenReturn(Optional.of(mockProductSize));

        // Espera a IllegalStateException que definimos no Service para o HTTP 422
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            checkoutService.processCheckout(userEmail, requestDto);
        });

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));

        // Verifica que o pedido e o item NUNCA foram salvos
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o tamanho do produto informado não existir no catálogo")
    void shouldThrowExceptionWhenProductSizeNotFound() {
        String userEmail = "cliente@teste.com";
        UUID addressId = UUID.randomUUID();
        UUID sizeId = UUID.randomUUID();

        User mockUser = User.builder().id(UUID.randomUUID()).email(userEmail).build();
        Address mockAddress = Address.builder().id(addressId).street("Endereço Teste").build();

        CheckoutRequestDTO requestDto = new CheckoutRequestDTO(
                addressId,
                List.of(new CheckoutItemDTO(sizeId, 1))
        );

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(productSizeRepository.findById(sizeId)).thenReturn(Optional.empty()); // Tamanho não existe

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.processCheckout(userEmail, requestDto);
        });

        assertTrue(exception.getMessage().contains("Tamanho de produto não encontrado"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}