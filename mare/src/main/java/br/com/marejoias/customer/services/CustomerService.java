package br.com.marejoias.customer.services;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.domain.entity.Customer;
import br.com.marejoias.customer.exception.MaxAddressesReachedException;
import br.com.marejoias.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void updateCustomer(UUID customerId, CustomerUpdateDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        customer.setName(dto.name());

        customerRepository.save(customer);
    }

    public void addAddress(UUID customerId, AddressCreateDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (customer.getAddresses().size() >= 3) {
            throw new MaxAddressesReachedException();
        }

        Address address = Address.builder()
                .zipCode(dto.zipCode())
                .street(dto.street())
                .build();

        customer.addAddress(address);
        customerRepository.save(customer);
    }
}