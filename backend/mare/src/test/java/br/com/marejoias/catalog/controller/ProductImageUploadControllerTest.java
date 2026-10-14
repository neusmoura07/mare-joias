package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.service.ProductService;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductImageUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturn200WhenImageIsUploadedSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        doNothing().when(productService).uploadProductImage(eq(productId), any());

        mockMvc.perform(multipart("/api/v1/products/" + productId + "/image")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenUploadingImageToNonExistentProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        doThrow(new IllegalArgumentException("Produto não encontrado para atualização de imagem"))
                .when(productService).uploadProductImage(eq(productId), any());

        mockMvc.perform(multipart("/api/v1/products/" + productId + "/image")
                        .file(file))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado para atualização de imagem"));
    }
}