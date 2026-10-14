package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.controller.dto.CheckoutItemDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.service.CheckoutService;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static br.com.marejoias.checkout.domain.entity.OrderStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false) // Desativa o Spring Security para focar no teste do endpoint
class CheckoutControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CheckoutService checkoutService;

    @Test
    @DisplayName("Deve retornar Status 201 e dados do pedido ao realizar o checkout com sucesso")
    void shouldReturn201AndOrderWhenCheckoutIsSuccessful() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Mock da resposta do pedido salvo
        Order pedidoMock = Order.builder()
                .id(orderId)
                .status(PENDING)
                .totalAmountCents(32500)
                .shippingFeeCents(0)
                .shippingAddress("Rua das Joias, 100")
                .build();

        // Agora o serviço espera o email do usuário e o novo DTO
        when(checkoutService.processCheckout(eq("cliente@teste.com"), any(CheckoutRequestDTO.class)))
                .thenReturn(pedidoMock);

        // Montando o NOVO DTO de requisição que o frontend enviará
        CheckoutRequestDTO requestDto = new CheckoutRequestDTO(
                UUID.randomUUID(), // addressId
                List.of(new CheckoutItemDTO(UUID.randomUUID(), 2)) // productSizeId e quantity
        );

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        // Fornece um Principal falso para o controller não estourar NullPointerException
                        .principal(() -> "cliente@teste.com"))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmountCents").value(32500));
    }
}