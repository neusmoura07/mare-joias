package br.com.marejoias.checkout.controller;

import br.com.marejoias.checkout.controller.dto.CheckoutItemDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutRequestDTO;
import br.com.marejoias.checkout.controller.dto.CheckoutResponseDTO;
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
    @DisplayName("Deve retornar Status 201 e a URL de pagamento ao realizar o checkout com sucesso")
    void shouldReturn201AndPaymentUrlWhenCheckoutIsSuccessful() throws Exception {
        UUID orderId = UUID.randomUUID();
        String fakePaymentUrl = "https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=123";

        // 1. Mock do serviço agora devolve o novo DTO em vez da entidade Order
        CheckoutResponseDTO responseMock = new CheckoutResponseDTO(orderId, fakePaymentUrl);

        when(checkoutService.processCheckout(eq("cliente@teste.com"), any(CheckoutRequestDTO.class)))
                .thenReturn(responseMock);

        // 2. Montando o DTO de requisição
        CheckoutRequestDTO requestDto = new CheckoutRequestDTO(
                UUID.randomUUID(), // addressId
                List.of(new CheckoutItemDTO(UUID.randomUUID(), 2)) // productSizeId e quantity
        );

        // 3. Executando e validando as novas chaves do JSON
        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(() -> "cliente@teste.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.paymentUrl").value(fakePaymentUrl));
    }
}