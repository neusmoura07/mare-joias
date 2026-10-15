package br.com.marejoias.customer.controller;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.services.CustomerService;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @MockBean
    TokenService tokenService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("Deve retornar 204 No Content ao atualizar o cliente")
    void shouldReturn204WhenUpdatingCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerUpdateDTO dto = new CustomerUpdateDTO("Nome Atualizado");

        mockMvc.perform(put("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        Mockito.verify(customerService, Mockito.times(1)).updateCustomer(eq(customerId), any(CustomerUpdateDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 201 Created e o ID do endereço ao adicionar via token")
    void shouldReturn201WhenAddingAddress() throws Exception {
        String email = "cliente@teste.com";
        UUID novoAddressId = UUID.randomUUID();
        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua das Flores");

        // Simula o service retornando o ID do endereço recém-criado
        Mockito.when(customerService.addAddressByEmail(eq(email), any(AddressCreateDTO.class)))
                .thenReturn(novoAddressId);

        mockMvc.perform(post("/api/v1/customers/me/addresses")
                        .principal(() -> email) // Simula o usuário autenticado (Principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(novoAddressId.toString())); // Verifica o retorno

        Mockito.verify(customerService, Mockito.times(1)).addAddressByEmail(eq(email), any(AddressCreateDTO.class));
    }
}