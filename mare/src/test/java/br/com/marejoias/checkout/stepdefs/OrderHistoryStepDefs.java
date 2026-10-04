package br.com.marejoias.checkout.stepdefs;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class OrderHistoryStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // E-mail fixo usado pelo step global "que estou autenticado na API como um Administrador"
    // (definido em ProductImageStepDefs, compartilhado por todo o glue code do Cucumber).
    private static final String ADMIN_EMAIL = "admin@marejoias.com";

    private ResultActions resultActions;
    private String currentCustomerEmail;
    private UUID currentOrderId;

    @Dado("que estou autenticado na API como o cliente {string}")
    public void queEstouAutenticadoNaApiComoOCliente(String email) {
        this.currentCustomerEmail = email;
        buscarOuCriarUsuario(email, Role.CUSTOMER);
    }

    @E("existem pedidos salvos para a {string} e para o {string}")
    public void existemPedidosSalvosParaAEParaO(String emailMaria, String emailJoao) {
        User maria = buscarOuCriarUsuario(emailMaria, Role.CUSTOMER);
        User joao = buscarOuCriarUsuario(emailJoao, Role.CUSTOMER);

        orderRepository.save(Order.builder()
                .user(maria)
                .status(OrderStatus.PAID)
                .totalAmountCents(10000)
                .shippingFeeCents(0)
                .shippingAddress("Endereco da Maria")
                .build());

        orderRepository.save(Order.builder()
                .user(joao)
                .status(OrderStatus.PAID)
                .totalAmountCents(20000)
                .shippingFeeCents(0)
                .shippingAddress("Endereco do Joao")
                .build());
    }

    @E("existe um pedido com o status {string}")
    public void existeUmPedidoComOStatus(String status) {
        User dono = buscarOuCriarUsuario("dono-pedido-" + System.currentTimeMillis() + "@marejoias.com", Role.CUSTOMER);

        orderRepository.save(Order.builder()
                .user(dono)
                .status(OrderStatus.valueOf(status))
                .totalAmountCents(30000)
                .shippingFeeCents(0)
                .shippingAddress("Endereco Generico")
                .build());
    }

    @Quando("eu fizer uma requisição GET para o meu histórico de pedidos")
    public void euFizerUmaRequisicaoGetParaOMeuHistoricoDePedidos() throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/orders/my-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(currentCustomerEmail).roles("CUSTOMER")));
    }

    @Quando("eu enviar uma requisição PATCH para atualizar o status deste pedido para {string}")
    public void euEnviarUmaRequisicaoPatchParaAtualizarOStatusDesteOedidoPara(String novoStatus) throws Exception {
        String body = "{\"status\": \"" + novoStatus + "\"}";

        // O pedido foi criado no step "existe um pedido com o status ..."
        this.currentOrderId = orderRepository.findTopByOrderByCreatedAtDesc().getId();

        resultActions = mockMvc.perform(patch("/api/v1/admin/orders/{orderId}/status", currentOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(user(ADMIN_EMAIL).roles("ADMIN")));
    }

    @Então("a API deve retornar o status HTTP {int}")
    public void aApiDeveRetornarOStatusHTTP(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }

    @E("a lista retornada deve conter apenas os pedidos da {string}")
    public void aListaRetornadaDeveConterApenasOsPedidosDa(String email) throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Endereco da Maria"));
    }

    @E("a lista não deve conter nenhum pedido do {string}")
    public void aListaNaoDeveConterNenhumPedidoDo(String email) throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(!body.contains("Endereco do Joao"));
    }

    // Nota: o step "Então o status do pedido na base de dados deve ser atualizado
    // para {string}" já é definido em PaymentWebhookStepDefs (texto idêntico) e
    // faz exatamente essa verificação a partir do mesmo pedido, por isso não é
    // redefinido aqui.

    private User buscarOuCriarUsuario(String email, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                .name("Usuário " + email)
                .email(email)
                .passwordHash("$2a$10$abcdefghijklmnopqrstuv")
                .role(role)
                .build()));
    }
}
