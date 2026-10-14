package br.com.marejoias.catalog.stepdefs;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.service.ProductService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CatalogStepDefs {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    // Mudamos de List<Product> para Page<Product> para adequar ao novo Service
    private Page<Product> result;

    @Dado("que existe um {string} ativo no banco de dados")
    public void queExisteUmProdutoAtivoNoBanco(String nomeDoProduto) {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Criando uma categoria genérica para evitar o INNER JOIN vazio do JPA
        Category categoria = categoryRepository.save(Category.builder()
                .name("Geral")
                .slug("geral")
                .build());

        Product ativo = Product.builder()
                .name(nomeDoProduto)
                .sku("SKU-1")
                .slug("anel-prata")
                .priceCents(10000)
                .stockQuantity(10)
                .isActive(true)
                .category(categoria)
                .build();
        productRepository.save(ativo);
    }

    @E("existe um {string} inativo no banco de dados")
    public void existeUmProdutoInativoNoBanco(String nomeDoProduto) {
        // Tenta pegar a categoria criada no passo anterior, ou cria uma nova
        Category categoria = categoryRepository.findAll().stream().findFirst()
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Geral")
                        .slug("geral")
                        .build()));

        Product inativo = Product.builder()
                .name(nomeDoProduto)
                .sku("SKU-2")
                .slug("colar-antigo")
                .priceCents(5000)
                .stockQuantity(0)
                .isActive(false)
                .category(categoria) // <-- Associando a categoria
                .build();
        productRepository.save(inativo);
    }

    @Dado("que existe um {string} da categoria {string}")
    public void queExisteUmProdutoDaCategoria(String nomeProduto, String slugCategoria) {
        // Garante banco limpo para o cenário de filtro
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category categoria = Category.builder()
                .name(slugCategoria.toUpperCase())
                .slug(slugCategoria)
                .build();
        categoryRepository.save(categoria);

        Product produto = Product.builder()
                .name(nomeProduto)
                .sku("SKU-" + nomeProduto.hashCode())
                .slug(nomeProduto.toLowerCase().replace(" ", "-"))
                .priceCents(15000)
                .stockQuantity(5)
                .isActive(true)
                .category(categoria)
                .build();
        productRepository.save(produto);
    }

    @E("existe uma {string} da categoria {string}")
    public void existeUmaProdutoDaCategoriaFeminino(String nomeProduto, String slugCategoria) {
        Category categoria = categoryRepository.findBySlug(slugCategoria).orElseGet(() -> {
            return categoryRepository.save(Category.builder()
                    .name(slugCategoria.toUpperCase())
                    .slug(slugCategoria)
                    .build());
        });

        Product produto = Product.builder()
                .name(nomeProduto)
                .sku("SKU-" + nomeProduto.hashCode())
                .slug(nomeProduto.toLowerCase().replace(" ", "-"))
                .priceCents(20000)
                .stockQuantity(5)
                .isActive(true)
                .category(categoria)
                .build();
        productRepository.save(produto);
    }

    @Quando("o sistema solicitar a lista de produtos da vitrine")
    public void oSistemaSolicitarAListaDeProdutosDaVitrine() {
        // Passa null para nome e categoria para buscar todos os ativos, simulando a página 0 com 10 itens
        this.result = productService.getProducts(null, null, PageRequest.of(0, 10));
    }

    @Quando("o sistema buscar os produtos pela categoria {string}")
    public void oSistemaBuscarOsProdutosPelaCategoria(String slugCategoria) {
        // Passa null para o nome, e a categoria específica
        this.result = productService.getProducts(null, slugCategoria, PageRequest.of(0, 10));
    }

    @Então("o sistema deve retornar {int} produto")
    public void oSistemaDeveRetornarProduto(int quantidadeEsperada) {
        // Extrai a lista de dentro do Page para verificar o tamanho
        assertEquals(quantidadeEsperada, result.getContent().size());
    }

    @E("o produto retornado deve ser o {string}")
    public void oProdutoRetornadoDeveSer(String nomeEsperado) {
        // Extrai a lista de dentro do Page para verificar o nome do primeiro item
        assertEquals(nomeEsperado, result.getContent().get(0).getName());
    }
}