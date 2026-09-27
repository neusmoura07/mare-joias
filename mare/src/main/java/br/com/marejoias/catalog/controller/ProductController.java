package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.controller.dto.ProductCreateDTO;
import br.com.marejoias.catalog.controller.dto.ProductDetailDTO;
import br.com.marejoias.catalog.controller.dto.ProductSizeCreateDTO;
import br.com.marejoias.catalog.controller.dto.ProductSummaryDTO;
import br.com.marejoias.catalog.controller.dto.ProductUpdateDTO;
import br.com.marejoias.catalog.controller.dto.StockUpdateDTO;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductSummaryDTO>> getProducts(
            @RequestParam(name = "categorySlug", required = false) String categorySlug) {

        List<Product> products;

        // Se o frontend mandar a categoria (ex: ?categorySlug=aneis), filtramos.
        // Se não mandar nada, devolvemos todos os produtos ativos.
        if (categorySlug != null && !categorySlug.isBlank()) {
            products = productService.getProductsByCategory(categorySlug);
        } else {
            products = productService.getActiveProducts();
        }

        // Convertido para DTO para não serializar o proxy lazy do Hibernate (Category)
        List<ProductSummaryDTO> response = products.stream().map(ProductSummaryDTO::from).toList();

        // Retorna Status 200 (OK) com a lista de produtos em formato JSON
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductDetailBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductCreateDTO dto) {
        Product product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDTO dto) {
        productService.updateProduct(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateProduct(@PathVariable UUID id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/sizes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProductSize(@PathVariable UUID productId, @RequestBody @Valid ProductSizeCreateDTO dto) {
        productService.createProductSize(productId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{productId}/sizes/{sizeId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSizeStock(
            @PathVariable UUID productId,
            @PathVariable UUID sizeId,
            @RequestBody @Valid StockUpdateDTO dto) {
        productService.updateSizeStock(productId, sizeId, dto);
        return ResponseEntity.noContent().build();
    }
}
