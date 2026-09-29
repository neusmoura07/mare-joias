package br.com.marejoias.catalog.stepdefs;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CatalogManagementStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResultActions resultActions;
    private String customerToken;

    @Dado("que existem produtos cadastrados e ativos no sistema")
    public void queExistemProdutosCadastradosEAtivosNoSistema() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // CRIAMOS UMA CATEGORIA PARA O JPQL NÃO FILTRAR (INNER JOIN) OS PRODUTOS
        Category categoria = categoryRepository.save(Category.builder()
                .name("Geral")
                .slug("geral-mgmt")
                .build());

        productRepository.save(Product.builder()
                .name("Anel de Prata")
                .sku("SKU-ATIVO-1")
                .slug("anel-de-prata-mgmt")
                .priceCents(10000)
                .stockQuantity(10)
                .isActive(true)
                .category(categoria) // <-- Associado aqui
                .build());

        productRepository.save(Product.builder()
                .name("Colar Antigo")
                .sku("SKU-INATIVO-1")
                .slug("colar-antigo-mgmt")
                .priceCents(5000)
                .stockQuantity(0)
                .isActive(false)
                .category(categoria) // <-- Associado aqui
                .build());
    }

    @Quando("um utilizador aceder à listagem de produtos da montra")
    public void umUtilizadorAcederAListagemDeProdutosDaMontra() throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/products").contentType(MediaType.APPLICATION_JSON));
    }

    @Então("o sistema deve retornar os produtos com status HTTP {int} OK")
    public void oSistemaDeveRetornarOsProdutosComStatusHTTP(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }

    @E("apenas os produtos com o estado ativo devem ser exibidos")
    public void apenasOsProdutosComOEstadoAtivoDevemSerExibidos() throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Anel de Prata"));
        assertTrue(!body.contains("Colar Antigo"));
    }

    @Dado("que existe uma categoria cadastrada com o slug {string} contendo produtos ativos")
    public void queExisteUmaCategoriaCadastradaComOSlugContendoProdutosAtivos(String slugCategoria) {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category categoria = categoryRepository.save(Category.builder()
                .name("Anel de Ouro")
                .slug(slugCategoria)
                .build());

        productRepository.save(Product.builder()
                .name("Anel de Ouro 18k")
                .sku("SKU-OURO-1")
                .slug("anel-de-ouro-18k-mgmt")
                .priceCents(50000)
                .stockQuantity(3)
                .isActive(true)
                .category(categoria)
                .build());

        Category outraCategoria = categoryRepository.save(Category.builder()
                .name("Pulseiras")
                .slug("pulseiras-mgmt")
                .build());

        productRepository.save(Product.builder()
                .name("Pulseira de Prata")
                .sku("SKU-PRATA-1")
                .slug("pulseira-de-prata-mgmt")
                .priceCents(20000)
                .stockQuantity(5)
                .isActive(true)
                .category(outraCategoria)
                .build());
    }

    @Quando("um cliente filtrar os produtos utilizando o slug {string}")
    public void umClienteFiltrarOsProdutosUtilizandoOSlug(String slugCategoria) throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/products")
                .param("categorySlug", slugCategoria)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Então("o sistema deve retornar apenas os produtos pertencentes a essa categoria")
    public void oSistemaDeveRetornarApenasOsProdutosPertencentesAEssaCategoria() throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Anel de Ouro 18k"));
        assertTrue(!body.contains("Pulseira de Prata"));
    }

    @E("retornar o status HTTP {int} OK")
    public void retornarOStatusHTTP(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }

    @Dado("que estou autenticado como um cliente comum CUSTOMER")
    public void queEstouAutenticadoComoUmClienteComumCUSTOMER() {
        String email = "cliente-" + System.currentTimeMillis() + "@marejoias.com";

        User customer = userRepository.save(User.builder()
                .name("Cliente Comum")
                .email(email)
                .passwordHash("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.CUSTOMER)
                .build());

        customerToken = tokenService.generateToken(customer);
    }

    @Quando("tentar enviar uma requisição POST para criar um novo produto no catálogo")
    public void tentarEnviarUmaRequisicaoPOSTParaCriarUmNovoProdutoNoCatalogo() throws Exception {
        Category categoria = categoryRepository.save(Category.builder()
                .name("Categoria Teste")
                .slug("categoria-teste-" + System.currentTimeMillis())
                .build());

        Map<String, Object> body = Map.of(
                "categoryId", categoria.getId().toString(),
                "sku", "SKU-BLOQUEADO-" + System.currentTimeMillis(),
                "name", "Produto Bloqueado",
                "slug", "produto-bloqueado-" + System.currentTimeMillis(),
                "priceCents", 10000,
                "stockQuantity", 5
        );

        resultActions = mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @Então("o sistema deve recusar a operação")
    public void oSistemaDeveRecusarAOperacao() throws Exception {
        int status = resultActions.andReturn().getResponse().getStatus();
        assertTrue(status >= 400, "Esperava uma resposta de recusa (>=400), mas obteve " + status);
    }

    @E("retornar o status HTTP {int} Forbidden")
    public void retornarOStatusHTTPForbidden(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }
}