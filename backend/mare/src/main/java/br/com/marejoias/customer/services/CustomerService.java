package br.com.marejoias.customer.services;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.domain.entity.Customer;
import br.com.marejoias.customer.exception.MaxAddressesReachedException;
import br.com.marejoias.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // O método antigo (addAddress por ID) pode ser removido se não for mais usado,
    // ou mantido para uso interno/admin. Substituímos pelo fluxo via e-mail:

    @Transactional
    public UUID addAddressByEmail(String email, AddressCreateDTO dto) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (customer.getAddresses().size() >= 3) {
            throw new MaxAddressesReachedException();
        }

        Address address = Address.builder()
                .zipCode(dto.zipCode())
                .street(dto.street())
                .build();

        customer.addAddress(address);

        // Salva e força o banco a devolver a entidade atualizada com os IDs gerados
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        // Pega o último endereço da lista (que acabou de ser inserido e tem o UUID)
        Address enderecoSalvo = savedCustomer.getAddresses().get(savedCustomer.getAddresses().size() - 1);

        return enderecoSalvo.getId();
    }
}