package br.com.marejoias.catalog.controller;

import br.com.marejoias.catalog.domain.entity.Category;
import br.com.marejoias.catalog.service.CategoryService;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve permitir que um ADMIN crie uma nova categoria")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToCreateCategory() throws Exception {
        Category categoriaCriada = Category.builder().id(UUID.randomUUID()).name("Anéis").slug("aneis").build();
        when(categoryService.createCategory(any())).thenReturn(categoriaCriada);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Anéis\", \"slug\": \"aneis\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("aneis"));
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN inative uma categoria")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToDeactivateCategory() throws Exception {
        mockMvc.perform(patch("/api/v1/categories/{id}/deactivate", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    // Nota: o bloqueio real de CUSTOMER via @PreAuthorize é validado no cenário de BDD
    // (CatalogManagementStepDefs), pois o slice @WebMvcTest não carrega o SecurityConfig
    // com @EnableMethodSecurity, então @PreAuthorize não é avaliado aqui.
}
