package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.service.ProductService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Desliga o Spring Security momentaneamente para testarmos apenas a rota
class ProductControllerTest {

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
}