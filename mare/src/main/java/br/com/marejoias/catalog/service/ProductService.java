package br.com.marejoias.catalog.service;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    // A injeção de dependência via construtor (Lombok gera isso para nós com o @RequiredArgsConstructor)
    private final ProductRepository productRepository;

    /**
     * Busca os produtos para a vitrine principal.
     * Regra de negócio: Só retorna produtos que estejam ativos.
     */
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Busca os produtos quando o usuário clica em uma categoria na Sidebar (ex: "aneis").
     */
    public List<Product> getProductsByCategory(String categorySlug) {
        return productRepository.findByIsActiveTrueAndCategorySlug(categorySlug);
    }
}