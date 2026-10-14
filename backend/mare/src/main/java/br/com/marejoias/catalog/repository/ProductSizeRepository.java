package br.com.marejoias.catalog.repository;

import br.com.marejoias.catalog.domain.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {

    // Lista todos os tamanhos cadastrados de um produto (usado na página de detalhes)
    List<ProductSize> findByProductId(UUID productId);

    // Garante que o tamanho pertence mesmo ao produto informado na URL
    Optional<ProductSize> findByIdAndProductId(UUID id, UUID productId);
}
