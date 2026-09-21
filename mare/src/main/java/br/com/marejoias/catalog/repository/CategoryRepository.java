package br.com.marejoias.catalog.repository;

import br.com.marejoias.catalog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    // O Spring Data é inteligente: lendo o nome do método "findBySlug", 
    // ele gera o SQL "SELECT * FROM categories WHERE slug = ?" sozinho!
    Optional<Category> findBySlug(String slug);
}