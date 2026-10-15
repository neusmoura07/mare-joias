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
        Customer mockCustomer = new Customer();

        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua Nova");

        Mockito.when(customerRepository.findByEmail(email)).thenReturn(Optional.of(mockCustomer));

        Mockito.when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            Address addedAddress = savedCustomer.getAddresses().get(0);

            ReflectionTestUtils.setField(addedAddress, "id", UUID.randomUUID());

            return savedCustomer;
        });

        UUID resultId = customerService.addAddressByEmail(email, dto);

        assertEquals(1, mockCustomer.getAddresses().size());
        assertEquals("Rua Nova", mockCustomer.getAddresses().get(0).getStreet());
        assertNotNull(resultId); // Agora o teste passará, pois o Mockito injetou o ID!
        Mockito.verify(customerRepository, Mockito.times(1)).save(mockCustomer);
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