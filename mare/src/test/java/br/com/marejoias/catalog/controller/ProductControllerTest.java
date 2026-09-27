package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.controller.dto.ProductDetailDTO;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.service.ProductService;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Desliga o Spring Security momentaneamente para testarmos apenas a rota
class ProductControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar Status 200 e lista de produtos ao buscar vitrine geral")
    void shouldReturn200AndProductsForGeneralShowcase() throws Exception {
        Product produtoMock = Product.builder()
                .id(UUID.randomUUID())
                .name("Colar de Prata")
                .priceCents(25000)
                .isActive(true)
                .build();

        when(productService.getActiveProducts()).thenReturn(List.of(produtoMock));

        // Simula uma requisição GET para /api/v1/products
        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Colar de Prata"))
                .andExpect(jsonPath("$[0].priceCents").value(25000));
    }

    @Test
    @DisplayName("Deve retornar Status 200 e produtos filtrados ao passar categorySlug")
    void shouldReturn200AndFilteredProductsWhenCategorySlugIsProvided() throws Exception {
        Product produtoMock = Product.builder()
                .id(UUID.randomUUID())
                .name("Aliança Ouro 18k")
                .isActive(true)
                .build();

        when(productService.getProductsByCategory("aneis")).thenReturn(List.of(produtoMock));

        // Simula GET para /api/v1/products?categorySlug=aneis
        mockMvc.perform(get("/api/v1/products")
                .param("categorySlug", "aneis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aliança Ouro 18k"));
    }

    @Test
    @DisplayName("Deve retornar Status 200 e o detalhe do produto com seus tamanhos")
    void shouldReturn200AndProductDetailWithSizes() throws Exception {
        ProductDetailDTO detalhe = new ProductDetailDTO(
                UUID.randomUUID(), "SKU-1", "Anel Solitário", "anel-solitario", "desc", "ouro",
                50000, "img.png", 5, true, "Anéis", "aneis", List.of());

        when(productService.getProductDetailBySlug("anel-solitario")).thenReturn(detalhe);

        mockMvc.perform(get("/api/v1/products/anel-solitario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anel Solitário"))
                .andExpect(jsonPath("$.categorySlug").value("aneis"));
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN crie um novo produto")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToCreateProduct() throws Exception {
        Product produtoCriado = Product.builder().id(UUID.randomUUID()).name("Anel Novo").isActive(true).build();
        when(productService.createProduct(any())).thenReturn(produtoCriado);

        String body = """
                {
                  "categoryId": "%s",
                  "sku": "SKU-1",
                  "name": "Anel Novo",
                  "slug": "anel-novo",
                  "priceCents": 10000,
                  "stockQuantity": 5
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Anel Novo"));
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN atualize o stock de um tamanho")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToUpdateSizeStock() throws Exception {
        mockMvc.perform(patch("/api/v1/products/{productId}/sizes/{sizeId}/stock", UUID.randomUUID(), UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stockQuantity\": 10}"))
                .andExpect(status().isNoContent());
    }
}
