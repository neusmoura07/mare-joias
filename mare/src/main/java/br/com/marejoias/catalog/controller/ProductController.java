package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(name = "categorySlug", required = false) String categorySlug) {
        
        List<Product> products;
        
        // Se o frontend mandar a categoria (ex: ?categorySlug=aneis), filtramos. 
        // Se não mandar nada, devolvemos todos os produtos ativos.
        if (categorySlug != null && !categorySlug.isBlank()) {
            products = productService.getProductsByCategory(categorySlug);
        } else {
            products = productService.getActiveProducts();
        }
        
        // Retorna Status 200 (OK) com a lista de produtos em formato JSON
        return ResponseEntity.ok(products);
    }
}