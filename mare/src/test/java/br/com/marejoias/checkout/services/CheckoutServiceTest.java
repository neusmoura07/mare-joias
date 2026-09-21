package br.com.marejoias.checkout.services;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.checkout.service.CheckoutService;
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
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    @DisplayName("Deve processar o checkout com sucesso quando houver estoque suficiente e calcular o total correto")
    void shouldProcessCheckoutSuccessfullyWhenStockIsSufficient() {
        UUID productId = UUID.randomUUID();
        Product produto = Product.builder()
                .id(productId)
                .name("Anel de Prata")
                .priceCents(15000) // R$ 150,00
                .stockQuantity(10)
                .isActive(true)
                .build();

        List<CheckoutService.ItemRequest> itens = List.of(
                new CheckoutService.ItemRequest(productId, 2) // 2 unidades = R$ 300,00
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(produto));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Frete de R$ 25,00 (2500 centavos). Total esperado: 30000 + 2500 = 32500 centavos
        Order result = checkoutService.processCheckout(itens, 2500, "Rua das Joias, 100");

        assertNotNull(result);
        assertEquals(32500, result.getTotalAmountCents());
        assertEquals("PENDING_PAYMENT", result.getStatus());
        verify(productRepository, times(1)).findById(productId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e impedir o checkout quando o estoque for insuficiente")
    void shouldThrowExceptionWhenStockIsInsufficient() {
        UUID productId = UUID.randomUUID();
        Product produto = Product.builder()
                .id(productId)
                .name("Brinco de Diamante")
                .priceCents(50000)
                .stockQuantity(1) // Apenas 1 em estoque
                .isActive(true)
                .build();

        List<CheckoutService.ItemRequest> itens = List.of(
                new CheckoutService.ItemRequest(productId, 2) // Tentando comprar 2
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(produto));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.processCheckout(itens, 0, "Endereço Teste");
        });

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o produto informado não existir no catálogo")
    void shouldThrowExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        List<CheckoutService.ItemRequest> itens = List.of(
                new CheckoutService.ItemRequest(productId, 1)
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.processCheckout(itens, 0, "Endereço Teste");
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}