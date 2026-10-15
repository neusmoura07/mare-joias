package br.com.marejoias.customer.controller;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCustomer(@PathVariable UUID id, @RequestBody CustomerUpdateDTO dto) {
        customerService.updateCustomer(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<Map<String, UUID>> addAddress(Principal principal, @RequestBody AddressCreateDTO dto) {
        UUID novoAddressId = customerService.addAddressByEmail(principal.getName(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("addressId", novoAddressId));
    }
}