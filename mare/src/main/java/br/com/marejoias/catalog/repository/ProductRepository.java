package br.com.marejoias.catalog.repository;

import br.com.marejoias.catalog.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE " +
            "(CAST(:name as string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name as string), '%'))) AND " +
            "(CAST(:categorySlug as string) IS NULL OR p.category.slug = :categorySlug) AND " +
            "(p.isActive = true)")
    Page<Product> findActiveProductsWithFilters(
            @Param("name") String name,
            @Param("categorySlug") String categorySlug,
            Pageable pageable);

    // Busca um produto específico pelo slug (usado na página de detalhes)
    Optional<Product> findBySlug(String slug);

    // Usado no cadastro/edição para validar unicidade antes de gravar
    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    // Usado ao inativar uma categoria, para inativar em cascata os produtos dela
    List<Product> findByCategoryId(UUID categoryId);
}