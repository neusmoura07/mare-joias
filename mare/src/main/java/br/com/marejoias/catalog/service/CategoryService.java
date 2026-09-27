package br.com.marejoias.catalog.service;

import br.com.marejoias.catalog.controller.dto.CategoryCreateDTO;
import br.com.marejoias.catalog.controller.dto.CategoryUpdateDTO;
import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.exception.DuplicateSlugException;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Category createCategory(CategoryCreateDTO dto) {
        if (categoryRepository.existsBySlug(dto.slug())) {
            throw new DuplicateSlugException();
        }

        Category category = Category.builder()
                .name(dto.name())
                .slug(dto.slug())
                .build();

        return categoryRepository.save(category);
    }

    public void updateCategory(UUID categoryId, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setName(dto.name());
        category.setSlug(dto.slug());

        categoryRepository.save(category);
    }

    /**
     * A tabela categories (migração V1) não possui coluna de status próprio,
     * então "inativar uma categoria" significa inativar todos os produtos vinculados a ela.
     */
    public void deactivateCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoria não encontrada");
        }

        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(product -> product.setIsActive(false));
        productRepository.saveAll(products);
    }
}
