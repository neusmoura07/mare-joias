package br.com.marejoias.customer.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String street;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;
}