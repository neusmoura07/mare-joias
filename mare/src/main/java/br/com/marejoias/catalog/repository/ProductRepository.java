package br.com.marejoias.catalog.repository;

import br.com.marejoias.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Busca todos os produtos que estão ativos (não foram deletados/inativados)
    List<Product> findByIsActiveTrue();

    // Busca produtos ativos de uma categoria específica usando o slug (ex: "aneis")
    List<Product> findByIsActiveTrueAndCategorySlug(String slug);
}