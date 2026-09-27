package br.com.marejoias.checkout.stepdefs;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.domain.entity.ProductSize;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderStatus;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.domain.entity.Customer;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CheckoutApiStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ResultActions resultActions;
    private UUID currentUserId;
    private UUID currentAddressId;
    private UUID currentSizeId;

    @Dado("que estou autenticado na API como cliente")
    public void que_estou_autenticado_na_api_como_cliente() {

        // 1. Criamos o Customer (que representa o utilizador logado)
        Customer cliente = Customer.builder()
                .name("Cliente da Silva")
                .email("cliente_checkout@teste.com")
                .cpf("77788899900")
                .passwordHash("senha123")
                .role("CUSTOMER")
                .build();

        // 2. Criamos o Address
        Address endereco = Address.builder()
                .street("Rua do Comércio, 123")
                .zipCode("00000-000")
                .build();

        // 3. Vinculamos usando o método do domínio
        cliente.addAddress(endereco);

        // 4. Salvamos. O Cascade do JPA vai salvar o endereço junto!
        cliente = customerRepository.save(cliente);

        this.currentUserId = cliente.getId();
        this.currentAddressId = cliente.getAddresses().get(0).getId();
    }

    @Dado("o produto {string} possui {int} unidades em estoque para o tamanho {string} custando {int} centavos")
    public void o_produto_possui_unidades_em_estoque_para_o_tamanho_custando_centavos(String nomeProduto, Integer estoque, String tamanho, Integer preco) {
        // 1. Criar Categoria Genérica
        Category categoria = Category.builder()
                .name("Geral")
                .slug("geral-" + System.currentTimeMillis())
                .build();
        categoria = categoryRepository.save(categoria);

        // 2. Criar Produto Raiz
        Product produto = Product.builder()
                .category(categoria)
                .name(nomeProduto)
                .slug(nomeProduto.toLowerCase().replace(" ", "-") + "-" + System.currentTimeMillis())
                .sku("SKU-" + System.currentTimeMillis())
                .priceCents(preco)
                .stockQuantity(estoque)
                .isActive(true)
                .build();
        produto = productRepository.save(produto);

        // 3. Criar o ProductSize (Opção 1: Tamanho Único ou Específico)
        ProductSize productSize = ProductSize.builder()
                .product(produto)
                .sizeName(tamanho)
                .stockQuantity(estoque)
                .build();
        productSize = productSizeRepository.save(productSize);

        this.currentSizeId = productSize.getId();
    }

    @Quando("eu enviar um POST de checkout comprando {int} unidades da {string} tamanho {string}")
    public void eu_enviar_um_post_de_checkout_comprando_unidades_da_tamanho(Integer quantidade, String nomeProduto, String tamanho) throws Exception {
        // Montamos o JSON da requisição com os IDs dinâmicos
        String jsonPayload = String.format("""
                {
                  "addressId": "%s",
                  "items": [
                    {
                      "sizeId": "%s",
                      "quantity": %d
                    }
                  ]
                }
                """, currentAddressId, currentSizeId, quantidade);

        resultActions = mockMvc.perform(post("/api/v1/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                // Bypass do Spring Security usando o e-mail exato cadastrado no banco
                .with(user("cliente_checkout@teste.com").roles("CUSTOMER")));
    }

    @Então("a API deve processar o pedido com sucesso")
    public void a_api_deve_processar_o_pedido_com_sucesso() throws Exception {
        resultActions.andExpect(status().isCreated());
    }

    @Então("o pedido deve ser salvo na base de dados com o status {string}")
    public void o_pedido_deve_ser_salvo_na_base_de_dados_com_o_status(String statusEsperado) {
        // Recupera o pedido feito por este usuário
        Order pedido = orderRepository.findAll().stream()
                // Assumindo que a sua entidade Order possui um mapeamento getUserId() ou getUser().getId()
                .filter(o -> o.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum pedido encontrado para o usuário"));

        assertEquals(OrderStatus.valueOf(statusEsperado), pedido.getStatus());
    }

    @Então("o valor total do pedido deve ser calculado como {int} centavos")
    public void o_valor_total_do_pedido_deve_ser_calculado_como_centavos(Integer totalEsperado) {
        Order pedido = orderRepository.findAll().get(0);
        assertEquals(totalEsperado, pedido.getTotalAmountCents());
    }

    @Então("o estoque do tamanho {string} do produto deve ser atualizado para {int} unidades")
    public void o_estoque_do_tamanho_do_produto_deve_ser_atualizado_para_unidades(String tamanho, Integer estoqueEsperado) {
        ProductSize productSize = productSizeRepository.findById(currentSizeId).orElseThrow();
        assertEquals(estoqueEsperado, productSize.getStockQuantity());
    }

    @Então("a API deve recusar o pedido")
    public void a_api_deve_recusar_o_pedido() {
        // Validação tratada no passo do HTTP 422
    }

    @Então("retornar uma mensagem de erro informando {string}")
    public void retornar_uma_mensagem_de_erro_informando(String mensagemEsperada) throws Exception {
        resultActions.andExpect(jsonPath("$.message").value(mensagemEsperada));
    }

    @Então("retornar o status HTTP {int}")
    public void retornar_o_status_http(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }
}