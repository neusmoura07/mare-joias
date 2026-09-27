package br.com.marejoias.catalog.services;

import br.com.marejoias.catalog.controller.dto.ProductCreateDTO;
import br.com.marejoias.catalog.controller.dto.ProductDetailDTO;
import br.com.marejoias.catalog.controller.dto.ProductSizeCreateDTO;
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
import br.com.marejoias.catalog.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductSizeRepository productSizeRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar apenas produtos ativos para a vitrine principal")
    void shouldReturnOnlyActiveProducts() {
        Product anelAtivo = Product.builder().name("Anel de Prata").isActive(true).build();
        when(productRepository.findByIsActiveTrue()).thenReturn(List.of(anelAtivo));

        List<Product> result = productService.getActiveProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Anel de Prata", result.get(0).getName());
        verify(productRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Deve retornar produtos filtrados corretamente pelo slug da categoria")
    void shouldReturnProductsFilteredByCategorySlug() {
        String slugDesejado = "aneis";
        Category categoriaAnel = Category.builder().slug(slugDesejado).build();
        Product anel = Product.builder().name("Anel Solitário").category(categoriaAnel).isActive(true).build();

        when(productRepository.findByIsActiveTrueAndCategorySlug(slugDesejado)).thenReturn(List.of(anel));

        List<Product> result = productService.getProductsByCategory(slugDesejado);

        assertFalse(result.isEmpty());
        assertEquals("Anel Solitário", result.get(0).getName());
        verify(productRepository, times(1)).findByIsActiveTrueAndCategorySlug(slugDesejado);
    }

    @Test
    @DisplayName("Deve retornar o detalhe do produto com seus tamanhos e stocks")
    void shouldReturnProductDetailWithSizes() {
        UUID productId = UUID.randomUUID();
        Category categoria = Category.builder().name("Anéis").slug("aneis").build();
        Product produto = Product.builder()
                .id(productId)
                .name("Anel Solitário")
                .slug("anel-solitario")
                .category(categoria)
                .isActive(true)
                .build();
        ProductSize tamanho = ProductSize.builder().id(UUID.randomUUID()).sizeName("18").stockQuantity(4).build();

        when(productRepository.findBySlug("anel-solitario")).thenReturn(Optional.of(produto));
        when(productSizeRepository.findByProductId(productId)).thenReturn(List.of(tamanho));

        ProductDetailDTO result = productService.getProductDetailBySlug("anel-solitario");

        assertEquals("Anel Solitário", result.name());
        assertEquals("aneis", result.categorySlug());
        assertEquals(1, result.sizes().size());
        assertEquals("18", result.sizes().get(0).sizeName());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhe de produto inexistente")
    void shouldThrowWhenProductDetailNotFound() {
        when(productRepository.findBySlug("nao-existe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductDetailBySlug("nao-existe"));
    }

    @Test
    @DisplayName("Deve criar um produto quando SKU e slug são únicos")
    void shouldCreateProductWhenSkuAndSlugAreUnique() {
        UUID categoryId = UUID.randomUUID();
        Category categoria = Category.builder().id(categoryId).name("Anéis").slug("aneis").build();
        ProductCreateDTO dto = new ProductCreateDTO(categoryId, "SKU-1", "Anel Novo", "anel-novo",
                "desc", "prata", 10000, "img.png", 5);

        when(productRepository.existsBySku("SKU-1")).thenReturn(false);
        when(productRepository.existsBySlug("anel-novo")).thenReturn(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoria));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.createProduct(dto);

        assertEquals("Anel Novo", result.getName());
        assertTrue(result.getIsActive());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com SKU duplicado")
    void shouldThrowWhenCreatingProductWithDuplicateSku() {
        ProductCreateDTO dto = new ProductCreateDTO(UUID.randomUUID(), "SKU-DUP", "Anel", "anel",
                null, null, 1000, null, 1);

        when(productRepository.existsBySku("SKU-DUP")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com slug duplicado")
    void shouldThrowWhenCreatingProductWithDuplicateSlug() {
        ProductCreateDTO dto = new ProductCreateDTO(UUID.randomUUID(), "SKU-1", "Anel", "anel-dup",
                null, null, 1000, null, 1);

        when(productRepository.existsBySku("SKU-1")).thenReturn(false);
        when(productRepository.existsBySlug("anel-dup")).thenReturn(true);

        assertThrows(DuplicateSlugException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados de um produto existente")
    void shouldUpdateExistingProduct() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Product produto = Product.builder().id(productId).name("Antigo").build();
        Category categoria = Category.builder().id(categoryId).build();
        ProductUpdateDTO dto = new ProductUpdateDTO(categoryId, "Novo Nome", "nova desc", "ouro", 20000, "novo.png");

        when(productRepository.findById(productId)).thenReturn(Optional.of(produto));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoria));

        productService.updateProduct(productId, dto);

        assertEquals("Novo Nome", produto.getName());
        assertEquals(20000, produto.getPriceCents());
        verify(productRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve inativar um produto existente")
    void shouldDeactivateExistingProduct() {
        UUID productId = UUID.randomUUID();
        Product produto = Product.builder().id(productId).isActive(true).build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(produto));

        productService.deactivateProduct(productId);

        assertFalse(produto.getIsActive());
        verify(productRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve criar um novo tamanho para um produto existente")
    void shouldCreateProductSize() {
        UUID productId = UUID.randomUUID();
        Product produto = Product.builder().id(productId).build();
        ProductSizeCreateDTO dto = new ProductSizeCreateDTO("16", 8);

        when(productRepository.findById(productId)).thenReturn(Optional.of(produto));
        when(productSizeRepository.save(any(ProductSize.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductSize result = productService.createProductSize(productId, dto);

        assertEquals("16", result.getSizeName());
        assertEquals(8, result.getStockQuantity());
    }

    @Test
    @DisplayName("Deve atualizar o stock de um tamanho específico")
    void shouldUpdateSizeStock() {
        UUID productId = UUID.randomUUID();
        UUID sizeId = UUID.randomUUID();
        ProductSize tamanho = ProductSize.builder().id(sizeId).sizeName("18").stockQuantity(2).build();
        StockUpdateDTO dto = new StockUpdateDTO(15);

        when(productSizeRepository.findByIdAndProductId(sizeId, productId)).thenReturn(Optional.of(tamanho));

        productService.updateSizeStock(productId, sizeId, dto);

        assertEquals(15, tamanho.getStockQuantity());
        verify(productSizeRepository, times(1)).save(tamanho);
    }
}
