package br.com.marejoias.identity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desabilita proteção contra ataques de formulários web (não usamos formulários, somos uma API REST)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Define que a API não guardará estado (sessão/cookies). Toda requisição é independente.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Mapeamento de rotas (A tabela de regras)
                .authorizeHttpRequests(authorize -> authorize
                        // Rotas públicas (Qualquer pessoa pode acessar sem Token)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll() // Cliente precisa ver os produtos sem estar logado
                        
                        // Rotas exclusivas de ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/**").hasRole("ADMIN") 
                        
                        // Qualquer outra rota exige estar autenticado (ex: Checkout, Perfil)
                        .anyRequest().authenticated()
                )
                
                // Pede para o Spring rodar o nosso filtro JWT ANTES do filtro padrão de autenticação dele
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Expõe o AuthenticationManager para podermos usá-lo lá no nosso AuthController na hora do login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Ensina o Spring Security qual algoritmo de criptografia usar para comparar as senhas (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}