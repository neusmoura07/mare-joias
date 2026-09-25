package br.com.marejoias.customer.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash = "default_hash";

    @Column(nullable = false)
    private String role = "CUSTOMER";

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }
}