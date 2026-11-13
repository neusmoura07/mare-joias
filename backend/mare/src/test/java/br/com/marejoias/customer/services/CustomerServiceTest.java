package br.com.marejoias.customer.services;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.domain.entity.Customer;
import br.com.marejoias.customer.exception.MaxAddressesReachedException;
import br.com.marejoias.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Deve atualizar o nome do cliente com sucesso")
    void shouldUpdateCustomerNameSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("Nome Antigo");

        CustomerUpdateDTO dto = new CustomerUpdateDTO("Nome Novo");

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        customerService.updateCustomer(customerId, dto);

        assertEquals("Nome Novo", mockCustomer.getName());
        Mockito.verify(customerRepository, Mockito.times(1)).save(mockCustomer);
    }

    @Test
    @DisplayName("Deve adicionar um endereço e retornar o UUID quando o cliente tiver menos de 3")
    void shouldAddAddressWhenUnderLimit() {
        String email = "cliente@teste.com";

        // 1. Criamos o cliente mockado com a lista vazia
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        mockCustomer.setAddresses(new ArrayList<>());

        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua Nova");

        // 2. Simulamos a busca
        org.mockito.Mockito.when(customerRepository.findByEmail(email))
                .thenReturn(Optional.of(mockCustomer));

        // 3. A SOLUÇÃO: Simulamos o saveAndFlush (que é o que o seu serviço realmente usa)
        org.mockito.Mockito.when(customerRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer customerSalvo = invocation.getArgument(0);

                    // Injetamos um UUID falso no endereço que acabou de ser adicionado
                    if (customerSalvo.getAddresses() != null && !customerSalvo.getAddresses().isEmpty()) {
                        Address ultimoEndereco = customerSalvo.getAddresses().get(customerSalvo.getAddresses().size() - 1);
                        org.springframework.test.util.ReflectionTestUtils.setField(ultimoEndereco, "id", UUID.randomUUID());
                    }

                    // Devolvemos o cliente! (Isto impede o NullPointerException no savedCustomer)
                    return customerSalvo;
                });

        // 4. Executamos o método
        UUID resultId = customerService.addAddressByEmail(email, dto);

        // 5. Validamos se deu tudo certo
        org.junit.jupiter.api.Assertions.assertEquals(1, mockCustomer.getAddresses().size());
        org.junit.jupiter.api.Assertions.assertEquals("Rua Nova", mockCustomer.getAddresses().get(0).getStreet());
        org.junit.jupiter.api.Assertions.assertNotNull(resultId);

        // 6. Verificamos se o saveAndFlush foi chamado
        org.mockito.Mockito.verify(customerRepository, org.mockito.Mockito.times(1)).saveAndFlush(mockCustomer);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar o quarto endereço")
    void shouldThrowExceptionWhenMaxAddressesReached() {
        String email = "cliente@teste.com";
        Customer mockCustomer = new Customer();

        mockCustomer.addAddress(new Address());
        mockCustomer.addAddress(new Address());
        mockCustomer.addAddress(new Address());

        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua Nova");

        Mockito.when(customerRepository.findByEmail(email)).thenReturn(Optional.of(mockCustomer));

        assertThrows(MaxAddressesReachedException.class, () -> customerService.addAddressByEmail(email, dto));

        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }
}