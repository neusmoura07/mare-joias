package br.com.marejoias.catalog.repository;

import br.com.marejoias.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Busca todos os produtos que estão ativos (não foram deletados/inativados)
    List<Product> findByIsActiveTrue();

    // Busca produtos ativos de uma categoria específica usando o slug (ex: "aneis")
    List<Product> findByIsActiveTrueAndCategorySlug(String slug);

    // Busca um produto específico pelo slug (usado na página de detalhes)
    Optional<Product> findBySlug(String slug);

    // Usado no cadastro/edição para validar unicidade antes de gravar
    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    // Usado ao inativar uma categoria, para inativar em cascata os produtos dela
    List<Product> findByCategoryId(UUID categoryId);
}