package br.com.marejoias.catalog.stepdefs;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductPaginationStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private ResultActions resultActions;

    @Dado("que existem produtos cadastrados no catálogo")
    public void queExistemProdutosCadastradosNoCatalogo() {
        // Se o banco estiver vazio, cria um produto genérico para testar a paginação
        if (productRepository.count() == 0) {
            Category cat = categoryRepository.save(Category.builder().name("Geral").slug("geral").build());
            productRepository.save(Product.builder()
                    .name("Produto Genérico")
                    .slug("produto-generico")
                    .sku("SKU-GEN-01")
                    .priceCents(1000)
                    .stockQuantity(10)
                    .isActive(true)
                    .category(cat)
                    .build());
        }
    }

    @Dado("que o catálogo possui o produto {string} e {string}")
    public void queOCatalogoPossuiOProdutoE(String produto1, String produto2) {
        Category cat = categoryRepository.save(Category.builder().name("Busca Nome").slug(UUID.randomUUID().toString()).build());
        
        productRepository.save(Product.builder().name(produto1).slug(UUID.randomUUID().toString()).sku(UUID.randomUUID().toString()).priceCents(1000).stockQuantity(10).isActive(true).category(cat).build());
        productRepository.save(Product.builder().name(produto2).slug(UUID.randomUUID().toString()).sku(UUID.randomUUID().toString()).priceCents(1000).stockQuantity(10).isActive(true).category(cat).build());
    }

    @Dado("que o catálogo possui o produto {string} na categoria {string} e {string} na categoria {string}")
    public void setupProdutosComCategoria(String prod1, String catName1, String prod2, String catName2) {
        Category cat1 = categoryRepository.save(Category.builder().name(catName1).slug(UUID.randomUUID().toString()).build());
        Category cat2 = categoryRepository.save(Category.builder().name(catName2).slug(UUID.randomUUID().toString()).build());

        productRepository.save(Product.builder().name(prod1).slug(UUID.randomUUID().toString()).sku(UUID.randomUUID().toString()).priceCents(1000).stockQuantity(10).isActive(true).category(cat1).build());
        productRepository.save(Product.builder().name(prod2).slug(UUID.randomUUID().toString()).sku(UUID.randomUUID().toString()).priceCents(1000).stockQuantity(10).isActive(true).category(cat2).build());
    }

    @Quando("eu requisitar a listagem de produtos paginada na página {int} com tamanho {int}")
    public void euRequisitarAListagemPaginada(int page, int size) throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/products")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));
    }

    @Quando("eu requisitar a listagem de produtos filtrando pelo nome {string}")
    public void euRequisitarAListagemFiltrandoPeloNome(String nome) throws Exception {
        resultActions = mockMvc.perform(get("/api/v1/products")
                .param("name", nome));
    }

    @Quando("eu requisitar a listagem de produtos filtrando pela categoria {string}")
    public void euRequisitarAListagemFiltrandoPelaCategoria(String categoria) throws Exception {
        Category cat = categoryRepository.findByName(categoria).orElseThrow();

        resultActions = mockMvc.perform(get("/api/v1/products")
                .param("categorySlug", cat.getSlug()));
    }


    @Então("o resultado HTTP retornado deve ser {int} OK")
    public void oResultadoHttpRetornadoDeveSerOk(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Então("a resposta deve conter uma página com os produtos")
    public void aRespostaDeveConterUmaPaginaComOsProdutos() throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("\"content\":"));
        assertTrue(body.contains("\"totalElements\":"));
    }

    @Então("a listagem deve conter o produto {string}")
    public void aListagemDeveConterOProduto(String nomeProduto) throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(body.contains(nomeProduto), "O produto " + nomeProduto + " deveria estar na lista.");
    }

    @Então("a listagem não deve conter o produto {string}")
    public void aListagemNaoDeveConterOProduto(String nomeProduto) throws Exception {
        String body = resultActions.andReturn().getResponse().getContentAsString();
        assertFalse(body.contains(nomeProduto), "O produto " + nomeProduto + " NÃO deveria estar na lista.");
    }
}