package br.com.marejoias.identity.controller.dto;

import br.com.marejoias.identity.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotBlank(message = "O nome é obrigatório") String name,
        @NotBlank(message = "O email é obrigatório") @Email String email,
        @NotBlank(message = "A senha é obrigatória") String password,
        @NotBlank(message = "O CPF é obrigatório") String cpf,
        @NotNull(message = "O perfil do usuário é obrigatório") Role role
) {}