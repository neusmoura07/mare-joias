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
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CucumberContextConfiguration
@SpringBootTest
public class CatalogStepDefs {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    private List<Product> result;

    @Dado("que existe um {string} ativo no banco de dados")
    public void queExisteUmProdutoAtivoNoBanco(String nomeDoProduto) {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Product ativo = Product.builder()
                .name(nomeDoProduto)
                .sku("SKU-1")
                .slug("anel-prata")
                .priceCents(10000)
                .stockQuantity(10)
                .isActive(true)
                .build();
        productRepository.save(ativo);
    }

    @E("existe um {string} inativo no banco de dados")
    public void existeUmProdutoInativoNoBanco(String nomeDoProduto) {
        Product inativo = Product.builder()
                .name(nomeDoProduto)
                .sku("SKU-2")
                .slug("colar-antigo")
                .priceCents(5000)
                .stockQuantity(0)
                .isActive(false)
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
        this.result = productService.getActiveProducts();
    }

    @Quando("o sistema buscar os produtos pela categoria {string}")
    public void oSistemaBuscarOsProdutosPelaCategoria(String slugCategoria) {
        this.result = productService.getProductsByCategory(slugCategoria);
    }

    @Então("o sistema deve retornar {int} produto")
    public void oSistemaDeveRetornarProduto(int quantidadeEsperada) {
        assertEquals(quantidadeEsperada, result.size());
    }

    @E("o produto retornado deve ser o {string}")
    public void oProdutoRetornadoDeveSer(String nomeEsperado) {
        assertEquals(nomeEsperado, result.get(0).getName());
    }
}