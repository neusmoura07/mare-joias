package br.com.marejoias.catalog.service;

import br.com.marejoias.catalog.controller.dto.ProductCreateDTO;
import br.com.marejoias.catalog.controller.dto.ProductDetailDTO;
import br.com.marejoias.catalog.controller.dto.ProductSizeCreateDTO;
import br.com.marejoias.catalog.controller.dto.ProductSizeDTO;
import br.com.marejoias.catalog.controller.dto.ProductUpdateDTO;
import br.com.marejoias.catalog.controller.dto.StockUpdateDTO;
import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.domain.entity.ProductSize;
import br.com.marejoias.catalog.exception.DuplicateSkuException;
import br.com.marejoias.catalog.exception.DuplicateSlugException;
import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSizeRepository productSizeRepository;

    private final ImageStorageService imageStorageService;

    /**
     * Busca os produtos de forma paginada.
     * Pode filtrar por nome ou slug da categoria, garantindo que apenas os ativos retornem.
     */
    public Page<Product> getProducts(String name, String categorySlug, Pageable pageable) {
        String filterName = (name != null && !name.isBlank()) ? name : null;
        String filterSlug = (categorySlug != null && !categorySlug.isBlank()) ? categorySlug : null;

        return productRepository.findActiveProductsWithFilters(filterName, filterSlug, pageable);
    }

    /**
     * Busca o detalhe de um produto (via slug) com todos os seus tamanhos e stocks disponíveis.
     */
    public ProductDetailDTO getProductDetailBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<ProductSizeDTO> sizes = productSizeRepository.findByProductId(product.getId()).stream()
                .map(size -> new ProductSizeDTO(size.getId(), size.getSizeName(), size.getStockQuantity()))
                .toList();

        Category category = product.getCategory();

        return new ProductDetailDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getMaterial(),
                product.getPriceCents(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getIsActive(),
                category != null ? category.getName() : null,
                category != null ? category.getSlug() : null,
                sizes
        );
    }

    public Product createProduct(ProductCreateDTO dto) {
        if (productRepository.existsBySku(dto.sku())) {
            throw new DuplicateSkuException();
        }
        if (productRepository.existsBySlug(dto.slug())) {
            throw new DuplicateSlugException();
        }

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Product product = Product.builder()
                .category(category)
                .sku(dto.sku())
                .name(dto.name())
                .slug(dto.slug())
                .description(dto.description())
                .material(dto.material())
                .priceCents(dto.priceCents())
                .imageUrl(dto.imageUrl())
                .stockQuantity(dto.stockQuantity())
                .isActive(true)
                .build();

        return productRepository.save(product);
    }

    public void updateProduct(UUID productId, ProductUpdateDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        product.setCategory(category);
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setMaterial(dto.material());
        product.setPriceCents(dto.priceCents());
        product.setImageUrl(dto.imageUrl());

        productRepository.save(product);
    }

    public void deactivateProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setIsActive(false);
        productRepository.save(product);
    }

    public ProductSize createProductSize(UUID productId, ProductSizeCreateDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ProductSize size = ProductSize.builder()
                .product(product)
                .sizeName(dto.sizeName())
                .stockQuantity(dto.stockQuantity())
                .build();

        return productSizeRepository.save(size);
    }

    public void updateSizeStock(UUID productId, UUID sizeId, StockUpdateDTO dto) {
        ProductSize size = productSizeRepository.findByIdAndProductId(sizeId, productId)
                .orElseThrow(() -> new RuntimeException("Tamanho de produto não encontrado"));

        size.setStockQuantity(dto.stockQuantity());
        productSizeRepository.save(size);
    }

    @Transactional
    public void uploadProductImage(UUID productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para atualização de imagem"));

        String imageUrl = imageStorageService.uploadImage(file);

        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }
}
