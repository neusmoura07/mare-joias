package br.com.marejoias.customer.controller;

import br.com.marejoias.customer.controller.dto.AddressCreateDTO;
import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Void> addAddress(@PathVariable UUID id, @RequestBody AddressCreateDTO dto) {
        customerService.addAddress(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}