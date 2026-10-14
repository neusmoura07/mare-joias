package br.com.marejoias.payment.stepdefs;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentWebhookStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private ResultActions resultActions;
    private UUID currentOrderId;

    @Dado("que existe um pedido com o status {string}")
    public void que_existe_um_pedido_com_o_status(String statusPedido) {
        // Criamos um User falso (com e-mail e CPF dinâmicos para evitar conflitos de unique constraint)
        User usuarioFalso = User.builder()
                .name("Cliente Falso")
                .email("falso_" + System.currentTimeMillis() + "@teste.com")
                .cpf("000" + (System.currentTimeMillis() % 100000000))
                .passwordHash("senha123")
                .role(Role.CUSTOMER)
                .build();
        usuarioFalso = userRepository.save(usuarioFalso);

        // Criamos o pedido direto no banco associado ao User convertendo a String para Enum
        Order pedido = Order.builder()
                .user(usuarioFalso)
                .status(OrderStatus.valueOf(statusPedido))
                .totalAmountCents(10000)
                .shippingFeeCents(0)
                .shippingAddress("Rua Teste")
                .build();

        pedido = orderRepository.save(pedido);
        this.currentOrderId = pedido.getId();
    }

    @Dado("que não existe nenhum pedido com o ID informado")
    public void que_nao_existe_nenhum_pedido_com_o_id_informado() {
        this.currentOrderId = UUID.randomUUID(); // Um ID que com certeza não está no banco
    }

    @Quando("o gateway enviar um webhook de pagamento com o status {string} para este pedido")
    public void o_gateway_enviar_um_webhook_de_pagamento_com_o_status_para_este_pedido(String statusPagamento) throws Exception {
        // O JSON DTO que o Controller vai receber (PaymentWebhookDTO)
        // O Jackson mapeará automaticamente a string do statusPagamento para o Enum PaymentResult
        String jsonPayload = String.format("""
                {
                  "orderId": "%s",
                  "paymentStatus": "%s"
                }
                """, currentOrderId, statusPagamento);

        // Dispara a requisição sem o .with(user(...)) porque webhooks são chamadas de máquina para máquina
        resultActions = mockMvc.perform(post("/api/v1/payments/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload));
    }

    @Quando("o gateway enviar um webhook de pagamento com o status {string} para um pedido inexistente")
    public void o_gateway_enviar_um_webhook_de_pagamento_com_o_status_para_um_pedido_inexistente(String statusPagamento) throws Exception {
        o_gateway_enviar_um_webhook_de_pagamento_com_o_status_para_este_pedido(statusPagamento);
    }

    @Então("a API de pagamentos deve retornar o status HTTP {int}")
    public void a_api_de_pagamentos_deve_retornar_o_status_http(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Então("o status do pedido na base de dados deve ser atualizado para {string}")
    public void o_status_do_pedido_na_base_de_dados_deve_ser_atualizado_para(String statusEsperado) {
        // Este step é compartilhado com outras features (ex.: checkout/order_history.feature).
        // Quando currentOrderId não foi definido por um "Dado" deste próprio arquivo,
        // cai para o pedido mais recentemente criado.
        UUID orderId = currentOrderId != null ? currentOrderId : orderRepository.findTopByOrderByCreatedAtDesc().getId();

        Order pedidoAtualizado = orderRepository.findById(orderId).orElseThrow();
        // A comparação agora ocorre entre Enums
        assertEquals(OrderStatus.valueOf(statusEsperado), pedidoAtualizado.getStatus());
    }

    @Então("a API de pagamentos deve recusar a operação")
    public void a_api_de_pagamentos_deve_recusar_a_operacao() {
        // Validação tratada no passo do código HTTP
    }

    @Então("a API de pagamentos deve retornar uma mensagem de erro informando {string}")
    public void a_api_de_pagamentos_deve_retornar_uma_mensagem_de_erro_informando(String mensagemEsperada) throws Exception {
        resultActions.andExpect(jsonPath("$.message").value(mensagemEsperada));
    }
}