package br.com.marejoias.catalog.services;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar apenas produtos ativos para a vitrine principal")
    void shouldReturnOnlyActiveProducts() {
        Product anelAtivo = Product.builder().name("Anel de Prata").isActive(true).build();
        when(productRepository.findByIsActiveTrue()).thenReturn(List.of(anelAtivo));

        List<Product> result = productService.getActiveProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Anel de Prata", result.get(0).getName());
        verify(productRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Deve retornar produtos filtrados corretamente pelo slug da categoria")
    void shouldReturnProductsFilteredByCategorySlug() {
        String slugDesejado = "aneis";
        Category categoriaAnel = Category.builder().slug(slugDesejado).build();
        Product anel = Product.builder().name("Anel Solitário").category(categoriaAnel).isActive(true).build();

        when(productRepository.findByIsActiveTrueAndCategorySlug(slugDesejado)).thenReturn(List.of(anel));

        List<Product> result = productService.getProductsByCategory(slugDesejado);

        assertFalse(result.isEmpty());
        assertEquals("Anel Solitário", result.get(0).getName());
        verify(productRepository, times(1)).findByIsActiveTrueAndCategorySlug(slugDesejado);
    }
}