package br.com.marejoias.catalog.services;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.service.ImageStorageService;
import br.com.marejoias.catalog.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageUploadServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldUploadImageAndSaveProductSuccessfully() {
        UUID productId = UUID.randomUUID();
        Product product = Product.builder().id(productId).build();
        MultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());
        String expectedUrl = "https://cloud.com/foto.jpg";

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(imageStorageService.uploadImage(file)).thenReturn(expectedUrl);

        productService.uploadProductImage(productId, file);

        assertEquals(expectedUrl, product.getImageUrl());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenUploadingImageToNonExistentProduct() {
        UUID productId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.uploadProductImage(productId, file);
        });

        assertEquals("Produto não encontrado para atualização de imagem", exception.getMessage());
        verify(imageStorageService, never()).uploadImage(any());
        verify(productRepository, never()).save(any());
    }
}