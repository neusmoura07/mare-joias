package br.com.marejoias.identity.service;

import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Deve retornar UserDetails quando o email for encontrado")
    void shouldReturnUserDetailsWhenEmailIsFound() {
        String email = "teste@teste.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails result = authorizationService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o email não existir")
    void shouldThrowExceptionWhenEmailIsNotFound() {
        String email = "inexistente@teste.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authorizationService.loadUserByUsername(email));
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(email);
    }
}