package br.com.marejoias.catalog.services;

import br.com.marejoias.catalog.controller.dto.CategoryCreateDTO;
import br.com.marejoias.catalog.controller.dto.CategoryUpdateDTO;
import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.exception.DuplicateSlugException;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve criar uma categoria quando o slug é único")
    void shouldCreateCategoryWhenSlugIsUnique() {
        CategoryCreateDTO dto = new CategoryCreateDTO("Anéis", "aneis");

        when(categoryRepository.existsBySlug("aneis")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.createCategory(dto);

        assertEquals("Anéis", result.getName());
        assertEquals("aneis", result.getSlug());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com slug duplicado")
    void shouldThrowWhenCreatingCategoryWithDuplicateSlug() {
        CategoryCreateDTO dto = new CategoryCreateDTO("Anéis", "aneis");

        when(categoryRepository.existsBySlug("aneis")).thenReturn(true);

        assertThrows(DuplicateSlugException.class, () -> categoryService.createCategory(dto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados de uma categoria existente")
    void shouldUpdateExistingCategory() {
        UUID categoryId = UUID.randomUUID();
        Category categoria = Category.builder().id(categoryId).name("Antigo").slug("antigo").build();
        CategoryUpdateDTO dto = new CategoryUpdateDTO("Novo Nome", "novo-slug");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoria));

        categoryService.updateCategory(categoryId, dto);

        assertEquals("Novo Nome", categoria.getName());
        assertEquals("novo-slug", categoria.getSlug());
        verify(categoryRepository, times(1)).save(categoria);
    }

    @Test
    @DisplayName("Deve inativar em cascata todos os produtos de uma categoria")
    void shouldDeactivateAllProductsWhenDeactivatingCategory() {
        UUID categoryId = UUID.randomUUID();
        Product produto1 = Product.builder().isActive(true).build();
        Product produto2 = Product.builder().isActive(true).build();

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(produto1, produto2));

        categoryService.deactivateCategory(categoryId);

        assertFalse(produto1.getIsActive());
        assertFalse(produto2.getIsActive());
        verify(productRepository, times(1)).saveAll(List.of(produto1, produto2));
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar categoria inexistente")
    void shouldThrowWhenDeactivatingNonExistentCategory() {
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> categoryService.deactivateCategory(categoryId));
        verify(productRepository, never()).findByCategoryId(any());
    }
}
