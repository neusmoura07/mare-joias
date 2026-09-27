package br.com.marejoias.payment.controller;

import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import br.com.marejoias.payment.controller.dto.PaymentResult;
import br.com.marejoias.payment.controller.dto.PaymentWebhookDTO;
import br.com.marejoias.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false) // Desliga a segurança apenas para focar no teste unitário do Controller
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private TokenService tokenService;

     @MockBean
     private UserRepository userRepository;

    @Test
    void shouldReturn200WhenPaymentIsProcessedSuccessfully() throws Exception {
        PaymentWebhookDTO dto = new PaymentWebhookDTO(UUID.randomUUID(), PaymentResult.SUCCESS);

        doNothing().when(paymentService).processPayment(any(PaymentWebhookDTO.class));

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenOrderIsNotFound() throws Exception {
        PaymentWebhookDTO dto = new PaymentWebhookDTO(UUID.randomUUID(), PaymentResult.SUCCESS);

        doThrow(new IllegalArgumentException("Pedido não encontrado"))
                .when(paymentService).processPayment(any(PaymentWebhookDTO.class));

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pedido não encontrado"));
    }

    @Test
    void shouldReturn422WhenOrderIsAlreadyPaid() throws Exception {
        PaymentWebhookDTO dto = new PaymentWebhookDTO(UUID.randomUUID(), PaymentResult.SUCCESS);

        doThrow(new IllegalStateException("Este pedido já foi pago anteriormente"))
                .when(paymentService).processPayment(any(PaymentWebhookDTO.class));

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Este pedido já foi pago anteriormente"));
    }
}