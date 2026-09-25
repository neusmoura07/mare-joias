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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
    @DisplayName("Deve adicionar um endereço quando o cliente tiver menos de 3")
    void shouldAddAddressWhenUnderLimit() {
        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();
        // Cliente começa sem endereços
        
        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua Nova");

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        customerService.addAddress(customerId, dto);

        assertEquals(1, mockCustomer.getAddresses().size());
        assertEquals("Rua Nova", mockCustomer.getAddresses().get(0).getStreet());
        Mockito.verify(customerRepository, Mockito.times(1)).save(mockCustomer);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar o quarto endereço")
    void shouldThrowExceptionWhenMaxAddressesReached() {
        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();

        mockCustomer.addAddress(new Address());
        mockCustomer.addAddress(new Address());
        mockCustomer.addAddress(new Address());

        AddressCreateDTO dto = new AddressCreateDTO("01000-000", "Rua Nova");

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(MaxAddressesReachedException.class, () -> customerService.addAddress(customerId, dto));

        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }
}