package br.com.marejoias.identity.controller;

import br.com.marejoias.identity.controller.dto.AuthenticationDTO;
import br.com.marejoias.identity.controller.dto.RegisterDTO;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desligamos a segurança real para testar apenas o Controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve retornar 201 Created quando o registo for bem sucedido")
    void shouldReturn201WhenRegistrationIsSuccessful() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("Teste", "teste@teste.com", "123456", "12345678900", Role.CUSTOMER);

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando tentar registar um email já existente")
    void shouldReturn400WhenEmailAlreadyExists() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("Teste", "duplicado@teste.com", "123456", "12345678900", Role.CUSTOMER);
        User existingUser = new User();

        Mockito.when(userRepository.findByEmail("duplicado@teste.com")).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o Token quando o login for bem sucedido")
    void shouldReturn200AndTokenWhenLoginIsSuccessful() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("teste@teste.com", "123456");
        User mockUser = new User();
        
        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);
        Mockito.when(tokenService.generateToken(mockUser)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }
}